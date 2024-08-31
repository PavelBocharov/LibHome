package com.mar.ds.views.card.tags;

import com.mar.ds.db.entity.CardType;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.provider.ListDataProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CardTagsView {

    private final MainView mainView;

    private Select<CardType> cardTypeListSelect;
    private Grid<CardTypeTag> tagsGrid;

    public void showDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeight(50, Unit.PERCENTAGE);
        dialog.setWidth(50, Unit.PERCENTAGE);

        List<CardType> cardTypeList = mainView.getRepositoryService().getCardTypeRepository().findAll();
        cardTypeListSelect = new Select<>();
        cardTypeListSelect.setLabel("Type");
        cardTypeListSelect.setEmptySelectionAllowed(false);
        cardTypeListSelect.setTextRenderer(CardType::getTitle);
        cardTypeListSelect.setDataProvider(new ListDataProvider<>(cardTypeList));
        cardTypeListSelect.setWidthFull();

        tagsGrid = new Grid<>();
        tagsGrid.addColumn(CardTypeTag::getId).setHeader("ID");
        tagsGrid.addColumn(CardTypeTag::getTitle).setHeader("Title");
        tagsGrid.addComponentColumn(tag -> new Button(
                VaadinIcon.CLOSE_CIRCLE.create(),
                event -> {
                    mainView.getRepositoryService().getCardTypeTagRepository().delete(tag);
                    reloadData();
                })
        ).setTextAlign(ColumnTextAlign.END);
        tagsGrid.setWidthFull();
        tagsGrid.setHeightFull();
//        tagsGrid.setHeight(70, Unit.PERCENTAGE);

        Button createTagBtn = new Button(
                "Create tag",
                VaadinIcon.PLUS.create(),
                event -> new CreateCardTagsView(mainView, this, cardTypeListSelect.getValue()).showDialog()
        );
        createTagBtn.setWidthFull();
        createTagBtn.setEnabled(false);

        cardTypeListSelect.addValueChangeListener(event -> {
            reloadData();
            createTagBtn.setEnabled(true);
        });

        HorizontalLayout btns =
                new HorizontalLayout(
                        createTagBtn,
                        ViewUtils.getCloseButton(dialog)
                );
        btns.setWidthFull();

        VerticalLayout data =
                new VerticalLayout(
                        cardTypeListSelect,
                        tagsGrid,
                        btns
                );
        data.setSizeFull();

        dialog.add(data);
        dialog.open();
    }

    public void reloadData() {
        CardType cardType = cardTypeListSelect.getValue();
        if (cardType != null) {
            List<CardTypeTag> tagList = mainView.getRepositoryService().getCardTypeTagRepository().findByCardType(cardType);
            tagsGrid.setItems(tagList);
            mainView.getCardView().reloadGrid();
        }
    }

}
