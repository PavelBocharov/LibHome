package com.mar.ds.views.card;

import com.mar.libhome.controller.data.PageRequest;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.utils.FileUtils;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.ContentView;
import com.mar.ds.views.MainView;
import com.mar.ds.views.build.pagination.PaginationGridService;
import com.mar.ds.views.card.status.CardStatusViewDialog;
import com.mar.ds.views.card.tags.CardTagsView;
import com.mar.ds.views.card.type.CardTypeViewDialog;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardTypeTagDto;
import com.mar.libhome.enums.GameEngine;
import com.mar.libhome.enums.Language;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.olli.FileDownloadWrapper;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.mar.ds.data.GridInfo.GRID_DATE_GAME;
import static com.mar.ds.data.GridInfo.GRID_DATE_UPD;
import static com.mar.ds.data.GridInfo.GRID_ENGINE;
import static com.mar.ds.data.GridInfo.GRID_ID;
import static com.mar.ds.data.GridInfo.GRID_LANGUAGE;
import static com.mar.ds.data.GridInfo.GRID_LINK;
import static com.mar.ds.data.GridInfo.GRID_POINT;
import static com.mar.ds.data.GridInfo.GRID_RATE;
import static com.mar.ds.data.GridInfo.GRID_STATUS;
import static com.mar.ds.data.GridInfo.GRID_TAGS;
import static com.mar.ds.data.GridInfo.GRID_TITLE;
import static com.mar.ds.data.GridInfo.GRID_TYPE;
import static com.mar.ds.db.diff.DiffCard.CARD_ENGINE;
import static com.mar.ds.db.diff.DiffCard.CARD_LANGUAGE;
import static com.mar.ds.db.diff.DiffCard.CARD_LAST_GAME_DATE;
import static com.mar.ds.db.diff.DiffCard.CARD_LAST_UPD_DATE;
import static com.mar.ds.db.diff.DiffCard.CARD_POINT;
import static com.mar.ds.db.diff.DiffCard.CARD_RATE;
import static com.mar.ds.db.diff.DiffCard.CARD_STATUS;
import static com.mar.ds.db.diff.DiffCard.CARD_TITLE;
import static com.mar.ds.db.diff.DiffCard.CARD_TYPE;
import static com.mar.ds.utils.FileUtils.getTitles;
import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static com.vaadin.flow.component.icon.VaadinIcon.BAR_CHART;
import static com.vaadin.flow.component.icon.VaadinIcon.COG;
import static com.vaadin.flow.component.icon.VaadinIcon.COGS;
import static com.vaadin.flow.component.icon.VaadinIcon.COMMENT_O;
import static com.vaadin.flow.component.icon.VaadinIcon.COMPILE;
import static com.vaadin.flow.component.icon.VaadinIcon.CUBES;
import static com.vaadin.flow.component.icon.VaadinIcon.DATE_INPUT;
import static com.vaadin.flow.component.icon.VaadinIcon.MEDAL;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;
import static com.vaadin.flow.component.icon.VaadinIcon.TEXT_LABEL;
import static java.lang.Math.abs;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
public class CardView implements ContentView {

    public static final int DEFAULT_GRID_ICON_SIZE_INT = 36;
    public static final String DEFAULT_GRID_ICON_SIZE_VAR = "var(--iron-icon-width, "
            + DEFAULT_GRID_ICON_SIZE_INT
            + "px)";

    private final MainView mainView;
    @Getter
    private final FileUtils.ViewTypeDto viewType;

    private int minPoint;
    private int maxPoint;
    private long minRate = Integer.MAX_VALUE;
    private long maxRate = Integer.MIN_VALUE;
    private Grid<com.mar.libhome.dto.CardDto> grid;
    private PaginationGridService<CardDto> paginationGridService;
    private int pageSize;

    public VerticalLayout getContent() {
        log.debug("Get content by {}", viewType);
        minPoint = Integer.parseInt(mainView.getEnv().getProperty("app.card.point.min", "0"));
        maxPoint = Integer.parseInt(mainView.getEnv().getProperty("app.card.point.max", "10"));
        pageSize = mainView.getEnv().getProperty("app.grid.row.count", Integer.class, 15);

        // TABLE
        grid = new Grid<>();
        TextField searchField = new TextField();
        paginationGridService = new PaginationGridService<CardDto>(grid, pageSize,
                data -> {
                    // calc min/max rate
                    List<CardDto> cardList = mainView.getCardService().findWithOrderByPoint(viewType.id());
                    for (CardDto card : cardList) {
                        double rate = card.getRate();
                        if (rate < minRate) {
                            minRate = (long) rate;
                        }
                        if (rate > maxRate) {
                            maxRate = (long) Math.ceil(rate);
                        }
                    }
                    if (maxRate <= minRate) {
                        maxRate = minRate + 1;
                    }

                    String searchText = getTextFieldValue(searchField).orElse("");
                    PageRequest.Sort sort = data.sortOrders();
                    PageRequest pageRequest = PageRequest.of(data.page(), data.pageSize(), sort);
                    if (isBlank(searchText)) {
                        return mainView.getCardService()
                                .findAllByView(viewType.id(), pageRequest);
                    } else {
                        return mainView.getCardService()
                                .findAllByViewAndLikeTitleMap(viewType.id(), searchText, pageRequest);
                    }
                }
        );
        // listeners
        initGridListeners();
        // column
        initGridColumn();

        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.setClearButtonVisible(true);
        searchField.addValueChangeListener(e -> paginationGridService.reloadData(1));

        // create view
        H3 label = new H3(viewType.title());
        label.setWidthFull();

        Select<Button> settingButtons = new Select<>();
        settingButtons.setPlaceholder("Settings");
        settingButtons.add(getBtns());

        Div div = new Div();
        div.setWidthFull();

        HorizontalLayout header = new HorizontalLayout(label, div, searchField, settingButtons);
        header.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, div, searchField, settingButtons);
        header.setWidthFull();

        header.setMaxHeight(40, Unit.PIXELS);
        grid.setSizeFull();

        VerticalLayout verticalLayout = new VerticalLayout(header, grid, paginationGridService.getPaginationButtons());
        verticalLayout.setSizeFull();
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, header);

        return verticalLayout;
    }

    private Component[] getBtns() {
        Button crtBtn = new Button(
                "Add",
                new Icon(PLUS),
                click -> new CreateCardView(mainView, viewType).showDialog()
        );
        crtBtn.setWidthFull();
        crtBtn.getStyle().set("color", "green");

        Button cardStatusView = new Button(
                "Status list", new Icon(COG), click -> new CardStatusViewDialog(mainView, this)
        );
        cardStatusView.setWidthFull();

        Button cardTypeView = new Button(
                "Types", new Icon(COMPILE), click -> new CardTypeViewDialog(mainView)
        );
        cardTypeView.setWidthFull();

        Button cardTypeTagView = new Button(
                "Type tags", new Icon(CUBES), click -> new CardTagsView(mainView).showDialog()
        );
        cardTypeTagView.setWidthFull();

        FileDownloadWrapper buttonWrapper = FileUtils.getDownloadFileButton(
                this.viewType.title() + ".xlsx",
                () -> mainView.getCardService().findWithOrderByPoint(viewType.id())
        );

        return new Component[]{crtBtn, cardStatusView, cardTypeView, cardTypeTagView, buttonWrapper};
    }

    private Component getEngineIcon(CardDto card) {
        try {
            GameEngine engine = GameEngine.DEFAULT;
            if (card != null && card.getEngine() != null) {
                engine = card.getEngine();
            }
            Image icon = new Image(engine.getIconPath(), engine.getName());
            icon.setTitle(engine.getName());
            icon.setHeight(DEFAULT_GRID_ICON_SIZE_VAR);
            icon.setWidth(DEFAULT_GRID_ICON_SIZE_VAR);
            icon.getStyle().set("margin-bottom", "-6px");
            return icon;
        } catch (Exception e) {
            e.printStackTrace();
            ViewUtils.showErrorMsg("Cannot load engine icon", e);
            return VaadinIcon.START_COG.create();
        }
    }

    private void openInfo(CardDto card) {
        if (card != null) {
            CardInfoView info = new CardInfoView(mainView, card, viewType);
            info.open();
        }
    }

    private Component getLabelWithColor(Supplier<Double> forColor, long min, long max) {
        double value = forColor.get() != null ? forColor.get() : 0;
        Label res = new Label(String.format("%.1f", value));

        String greenHex = "3cb043";
        String redHex = "c91203";
        Color colorTo = new Color(
                Integer.valueOf(greenHex.substring(0, 2), 16),
                Integer.valueOf(greenHex.substring(2, 4), 16),
                Integer.valueOf(greenHex.substring(4, 6), 16)
        );
        Color colorFrom = new Color(
                Integer.valueOf(redHex.substring(0, 2), 16),
                Integer.valueOf(redHex.substring(2, 4), 16),
                Integer.valueOf(redHex.substring(4, 6), 16)
        );

        String hex = String.format("#%02x%02x%02x",
                calcGradient(colorFrom.getRed(), colorTo.getRed(), min, max, (int) value),
                calcGradient(colorFrom.getGreen(), colorTo.getGreen(), min, max, (int) value),
                calcGradient(colorFrom.getBlue(), colorTo.getBlue(), min, max, (int) value)
        );

        res.getStyle().set("color", hex);
        return res;
    }

    private Component getLabelWithColor(Supplier<Double> forColor) {
        return getLabelWithColor(forColor, minPoint, maxPoint);
    }

    private int calcGradient(int colorFrom, int colorTo, long min, long max, int point) {
        long steps = abs(min - max);
        double colorStep = (double) (colorTo - colorFrom) / steps;
        int bufValue = point;
        double color = colorFrom;
        while (bufValue > min) {
            color += colorStep;
            bufValue--;
        }
        return (int) color;
    }

    @Override
    public void reloadData() {
        paginationGridService.reloadData();
    }

    private void initGridListeners() {
        Map<String, String> gridConfig = getTitles(viewType, mainView.getContentJson());

        grid.addItemDoubleClickListener(
                dialogItemDoubleClickEvent -> openInfo(dialogItemDoubleClickEvent.getItem())
        );

        GridContextMenu<CardDto> menu = grid.addContextMenu();
        menu.addItem("View", event -> event.getItem().ifPresent(this::openInfo));
        if (gridConfig.containsKey(GRID_LINK)) {
            menu.addItem("Link", event -> event.getItem().ifPresent(card -> {
                if (isBlank(card.getLink())) {
                    ViewUtils.showErrorMsg(
                            "Not find URL",
                            new Exception("URL field is blank. Set value in card info dialog update view.")
                    );
                } else {
                    mainView.getUI().ifPresent(ui -> ui.getPage().open(card.getLink(), "_blank"));
                }
            }));
        }
        menu.addItem(
                "Fast edit",
                event -> event.getItem().ifPresent(
                        card -> new FastUpdateCardView(mainView, card, viewType).showDialog()
                )
        );
        menu.addItem("History", event -> {
            event.getItem().ifPresent(card -> new CardHistoryView(mainView, card).open());
        });
        menu.addItem("Delete", event ->
                        event.getItem().ifPresent(card -> new DeleteDialogWidget(() -> {
//                    CardDto cardDto = Mappers.getMapper(CardMapper.class).toDto(card);
                            mainView.getCardService().remove(card);
                            mainView.getCardHistoryService().saveDeleteCard(card);
                            reloadData();
                            FileUtils.deleteDir(mainView.getEnv().getProperty("app.data.path") + "cards/" + card.getId());
                        }))
        );
    }

    private void initGridColumn() {
        Map<String, String> gridConfig = getTitles(viewType, mainView.getContentJson());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        assert nonNull(gridConfig);

        if (gridConfig.containsKey(GRID_ID)) {
            grid.addColumn(CardDto::getId)
                    .setHeader(gridConfig.get(GRID_ID))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.START)
                    .setId(GRID_ID);
        }
        if (gridConfig.containsKey(GRID_STATUS)) {
            grid.addComponentColumn(ViewUtils::getStatusIcon)
                    .setHeader(paginationGridService.getHeader(
                            MEDAL, gridConfig.get(GRID_STATUS), CARD_STATUS
                    ))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_STATUS);
        }
        if (gridConfig.containsKey(GRID_ENGINE)) {
            grid.addComponentColumn(this::getEngineIcon)
                    .setHeader(paginationGridService.getHeader(COGS, gridConfig.get(GRID_ENGINE), CARD_ENGINE))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_ENGINE);
        }
        if (gridConfig.containsKey(GRID_LANGUAGE)) {
            grid.addComponentColumn(card ->
                            ViewUtils.getImage(Optional
                                            .ofNullable(card.getLanguage())
                                            .orElse(Language.DEFAULT),
                                    DEFAULT_GRID_ICON_SIZE_INT
                            )
                    )
                    .setHeader(paginationGridService.getHeader(
                            COMMENT_O, gridConfig.get(GRID_LANGUAGE), CARD_LANGUAGE
                    ))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_LANGUAGE);
        }
        if (gridConfig.containsKey(GRID_TITLE)) {
            grid.addColumn(CardDto::getTitle)
                    .setHeader(paginationGridService.getHeader(
                            TEXT_LABEL, gridConfig.get(GRID_TITLE), CARD_TITLE
                    ))
                    .setAutoWidth(true)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_TITLE);
        }
        if (gridConfig.containsKey(GRID_POINT)) {
            grid.addComponentColumn(card -> getLabelWithColor(card::getPoint))
                    .setHeader(paginationGridService.getHeader(MEDAL, gridConfig.get(GRID_POINT), CARD_POINT))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_POINT);
        }
        if (gridConfig.containsKey(GRID_RATE)) {
            grid.addComponentColumn(card -> getLabelWithColor(card::getRate, minRate, maxRate))
                    .setHeader(paginationGridService.getHeader(
                            BAR_CHART, gridConfig.get(GRID_RATE), CARD_RATE
                    ))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_RATE);
        }

        if (gridConfig.containsKey(GRID_DATE_UPD)) {
            grid.addColumn(card -> dateFormat.format(card.getLastUpdate()))
                    .setHeader(paginationGridService.getHeader(
                            DATE_INPUT, gridConfig.get(GRID_DATE_UPD), CARD_LAST_UPD_DATE)
                    )
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_DATE_UPD);
        }
        if (gridConfig.containsKey(GRID_DATE_GAME)) {
            grid.addColumn(card -> dateFormat.format(card.getLastGame()))
                    .setHeader(paginationGridService.getHeader(
                            DATE_INPUT, gridConfig.get(GRID_DATE_GAME), CARD_LAST_GAME_DATE
                    ))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_DATE_GAME);
        }
        if (gridConfig.containsKey(GRID_TYPE)) {
            grid.addColumn(card -> card.getCardType().getTitle())
                    .setAutoWidth(true).setFlexGrow(0)
                    .setHeader(paginationGridService.getHeader(
                            COMPILE, gridConfig.get(GRID_TYPE), CARD_TYPE
                    ))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_TYPE);
        }
        if (gridConfig.containsKey(GRID_TAGS)) {
            grid.addColumn(card -> card.getTagList() == null || card.getTagList().isEmpty()
                            ? "---"
                            : card.getTagList().stream().map(CardTypeTagDto::getTitle).collect(Collectors.joining(", "))
                    )
                    .setHeader(gridConfig.get(GRID_TAGS))
                    .setAutoWidth(true)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_TAGS);
        }
    }

}
