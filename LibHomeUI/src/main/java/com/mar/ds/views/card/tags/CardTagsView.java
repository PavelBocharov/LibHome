package com.mar.ds.views.card.tags;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardType;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.dto.CardTypeTagDto;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.provider.ListDataProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static java.lang.String.format;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
public class CardTagsView {

    private final MainView mainView;

    private Select<CardTypeDto> cardTypeListSelect;
    private Grid<CardTypeTagDto> tagsGrid;

    public void showDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeight(50, Unit.PERCENTAGE);
        dialog.setWidth(50, Unit.PERCENTAGE);

        List<CardTypeDto> cardTypeList = mainView.getCardTypeService().findAll();
        cardTypeListSelect = new Select<>();
        cardTypeListSelect.setPlaceholder("Type");
        cardTypeListSelect.setEmptySelectionAllowed(false);
        cardTypeListSelect.setTextRenderer(CardTypeDto::getTitle);
        cardTypeListSelect.setDataProvider(new ListDataProvider<>(cardTypeList));
        cardTypeListSelect.setWidthFull();

        tagsGrid = new Grid<>();
        tagsGrid.addColumn(CardTypeTagDto::getId).setHeader("ID")
                .setAutoWidth(true).setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.START);
        tagsGrid.addColumn(CardTypeTagDto::getTitle).setHeader("Title")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER);
        tagsGrid.addComponentColumn(
                        tag -> {
                            Button dltBtn = new Button(
                                    VaadinIcon.CLOSE_CIRCLE.create(),
                                    event -> {
                                        List<CardDto> cards = mainView.getCardService().findByTag(tag.getId());
                                        if (isEmpty(cards)) {
                                            log.info("Delete card status tag: {}", tag);
                                            mainView.getCardTypeTagService().delete(tag);
                                            reloadData();
                                        } else {
                                            log.warn("Find cards with status tag: {}, list: {}", tag, cards);
                                            ViewUtils.showErrorMsg(
                                                    "Delete card type tag ERROR",
                                                    new Exception(
                                                            format(
                                                                    "Find cards with type tag: '%s', count: %d.",
                                                                    tag.getTitle(), cards.size()
                                                            )
                                                    )
                                            );
                                        }
                                    });
                            dltBtn.getStyle().set("color", "red");
                            HorizontalLayout btns = new HorizontalLayout(
                                    dltBtn,
                                    new Button(
                                            VaadinIcon.PENCIL.create(),
                                            event -> {
                                                new UpdateCardTagsView(mainView, this, tag).showDialog();
                                            })
                            );
                            return btns;
                        }
                )
                .setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
        tagsGrid.setWidthFull();
        tagsGrid.setHeightFull();

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

        Label label = new Label("Type tag list");
        label.setSizeFull();
        HorizontalLayout head = new HorizontalLayout(
                label,
                cardTypeListSelect
        );
        head.getStyle().set("padding", "0px");
        head.setWidthFull();

        VerticalLayout data =
                new VerticalLayout(
                        head,
                        tagsGrid,
                        btns
                );
        data.setSizeFull();
        data.getStyle().set("padding", "0px");

        dialog.add(data);
        dialog.open();
    }

    public void reloadData() {
        CardTypeDto cardType = cardTypeListSelect.getValue();
        if (cardType != null) {
            List<CardTypeTagDto> tagList = mainView
                    .getCardTypeTagService()
                    .findByCardType(cardType);
            tagsGrid.setItems(tagList);
            mainView.getActiveView().reloadData();
        }
    }

}
