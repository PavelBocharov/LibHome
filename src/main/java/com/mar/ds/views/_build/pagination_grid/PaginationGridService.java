package com.mar.ds.views._build.pagination_grid;

import com.mar.ds.utils.ButtonBuilder;
import com.mar.ds.utils.ViewUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static com.vaadin.flow.component.icon.VaadinIcon.ANGLE_DOUBLE_LEFT;
import static com.vaadin.flow.component.icon.VaadinIcon.ANGLE_DOUBLE_RIGHT;
import static com.vaadin.flow.component.icon.VaadinIcon.ANGLE_DOWN;
import static com.vaadin.flow.component.icon.VaadinIcon.ANGLE_LEFT;
import static com.vaadin.flow.component.icon.VaadinIcon.ANGLE_RIGHT;
import static com.vaadin.flow.component.icon.VaadinIcon.ANGLE_UP;
import static com.vaadin.flow.component.icon.VaadinIcon.ELLIPSIS_DOTS_H;

@Slf4j
public class PaginationGridService<T> {

    public static final String GRID_COLUMN_SORT_ASC_SUFFIX = "-sort-asc";
    public static final String GRID_COLUMN_SORT_DESC_SUFFIX = "-sort-desc";

    private final Grid<T> grid;
    private final int gridPageSize;
    private final Function<GetData, Page<T>> getDataFunction;
    private final Map<String, Sort.Direction> directionMap = new HashMap<>();

    private final IntegerField pageField;
    private final Label countPageLabel;

    public PaginationGridService(Grid<T> grid, int gridPageSize, Function<GetData, Page<T>> getDataFunction) {
        this.grid = grid;
        this.gridPageSize = gridPageSize;
        this.getDataFunction = getDataFunction;

        pageField = new IntegerField();
        countPageLabel = new Label("???");

        initGridData(0);
    }

    public Component getHeader(VaadinIcon headerIcon, String text, String columnName) {
        Icon mainIcon = new Icon(headerIcon);
        final String buttonId = UUID.randomUUID().toString();

        mainIcon.setId(buttonId);
        Button button = new Button(text, mainIcon);
        button.addClickListener(event -> {
            Icon icon = (Icon) button.getIcon();
            String id = icon.getId().orElse("").trim();

            if (buttonId.equals(id)) {
                Icon downIcon = new Icon(ANGLE_DOWN);
                downIcon.setId(buttonId + GRID_COLUMN_SORT_DESC_SUFFIX);
                button.setIcon(downIcon);
                directionMap.put(columnName, Sort.Direction.DESC);
            } else if (id.endsWith(GRID_COLUMN_SORT_DESC_SUFFIX)) {
                Icon upIcon = new Icon(ANGLE_UP);
                upIcon.setId(buttonId + GRID_COLUMN_SORT_ASC_SUFFIX);
                button.setIcon(upIcon);
                directionMap.put(columnName, Sort.Direction.ASC);
            } else {
                directionMap.remove(columnName);
                button.setIcon(mainIcon);
            }

            this.initGridData(0);
        });
        return button;
    }

    public HorizontalLayout getPaginationButtons() {
        HorizontalLayout btns = new HorizontalLayout();
        btns.setAlignItems(FlexComponent.Alignment.CENTER);

        pageField.addKeyPressListener(Key.ENTER, event -> {
            int newPage = saveCastInt(pageField.getValue(), -1);
            int totalCntPage = saveParseInt(countPageLabel.getText(), -1);
            if (newPage > 0 && newPage <= totalCntPage) {
                initGridData(newPage - 1);
            } else {
                ViewUtils.showErrorMsg(
                        "Incorrect search page number.",
                        new RuntimeException("Need 'newPage > 0 && newPage <= totalCntPage'")
                );
            }
        });

        Icon dots = new Icon(ELLIPSIS_DOTS_H);

        Button llBtn = ButtonBuilder.createButton()
                .icon(ANGLE_DOUBLE_LEFT)
                .color(ButtonBuilder.Color.BLACK)
                .clickListener(btnClick -> initGridData(0))
                .build();

        Button lBtn = ButtonBuilder.createButton()
                .icon(ANGLE_LEFT)
                .color(ButtonBuilder.Color.BLACK)
                .clickListener(btnClick -> {
                    int newPage = saveCastInt(pageField.getValue(), 1);
                    if (newPage > 1) {
                        newPage--;
                    }
                    initGridData(newPage - 1);
                })
                .build();

        Button rBtn = ButtonBuilder.createButton()
                .icon(ANGLE_RIGHT)
                .color(ButtonBuilder.Color.BLACK)
                .clickListener(btnClick -> {
                    int totalCntPage = saveParseInt(countPageLabel.getText(), 1);
                    int newPage = saveCastInt(pageField.getValue(), totalCntPage);
                    if (newPage < totalCntPage) {
                        newPage++;
                    } else {
                        newPage = totalCntPage;
                    }
                    initGridData(newPage - 1);
                })
                .build();
        Button rrBtn = ButtonBuilder.createButton()
                .icon(ANGLE_DOUBLE_RIGHT)
                .color(ButtonBuilder.Color.BLACK)
                .clickListener(btnClick -> {
                    int totalCntPage = saveParseInt(countPageLabel.getText(), 1);
                    initGridData(totalCntPage - 1);
                })
                .build();

        Div leftSpace = new Div();
        leftSpace.setWidthFull();
        Div rightSpace = new Div();
        rightSpace.setWidthFull();

        btns.add(
                leftSpace, llBtn, lBtn,
                pageField, dots, countPageLabel,
                rBtn, rrBtn, rightSpace
        );
        btns.setWidthFull();
        return btns;
    }

    public void reloadData() {
        initGridData(saveCastInt(pageField.getValue() - 1, -1));
    }

    private void initGridData(int page) {
        List<Sort.Order> sortOrders = new ArrayList<>(directionMap.size());
        for (String columnName : directionMap.keySet()) {
            sortOrders.add(new Sort.Order(directionMap.get(columnName), columnName));
        }

        Page<T> cardPage = getDataFunction.apply(new GetData(page, gridPageSize, sortOrders));
        List<T> typeList = cardPage.getContent();

        grid.setItems(typeList);

        pageField.setValue(page + 1);
        countPageLabel.setText(String.valueOf((int) Math.ceil(cardPage.getTotalElements() * 1.0 / gridPageSize)));
    }

    private int saveParseInt(String number, int defNumb) {
        try {
            return Integer.parseInt(number);
        } catch (Exception ex) {
            return defNumb;
        }
    }

    private int saveCastInt(Integer number, int defNumb) {
        if (number == null) {
            return defNumb;
        }
        return number;
    }

    public record GetData(int page, int pageSize, List<Sort.Order> sortOrders) { }
}
