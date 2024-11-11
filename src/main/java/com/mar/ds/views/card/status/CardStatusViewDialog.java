package com.mar.ds.views.card.status;

import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.service.CardStatusService;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.ContentView;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CardStatusViewDialog {
    private final MainView mainView;
    private final ContentView parentView;
    private Dialog dialog;
    private Grid<CardStatus> cardStatusList;
    private Button crtBtn;

    public CardStatusViewDialog(MainView appLayout, ContentView parentView) {
        this.mainView = appLayout;
        this.parentView = parentView;

        dialog = new Dialog();

        crtBtn = new Button("Create card status", new Icon(VaadinIcon.PLUS));
        crtBtn.setWidthFull();
        crtBtn.addClickListener(btnClick -> new CreateCardStatusView(this));

        reloadData();

        dialog.open();
    }


    private void initProducts() {
        cardStatusList = new Grid<>();
        cardStatusList.setSizeFull();
        cardStatusList.setMaxHeight(80, Unit.PERCENTAGE);
        cardStatusList.setVerticalScrollingEnabled(true);

        cardStatusList.addColumn(CardStatus::getTitle)
                .setHeader("Title")
                .setSortable(true);
        cardStatusList.addColumn(CardStatus::getOrder)
                .setHeader("Order")
                .setSortable(true);
        cardStatusList.addColumn(CardStatus::getIsRate)
                .setHeader("Is rate")
                .setSortable(true);
        cardStatusList.addColumn(CardStatus::getHasUpdStatus)
                .setHeader("Has UPD")
                .setSortable(true);
        cardStatusList.addComponentColumn(cardStatus -> {
                    Button dltBtn = new Button(new Icon(VaadinIcon.BAN), buttonClickEvent -> {
                        try {
                            new DeleteDialogWidget(() -> {
                                try {
                                    getService().delete(cardStatus);
                                    reloadData();
                                } catch (Exception e) {
                                    ViewUtils.showErrorMsg("Delete card status ERROR", e);
                                }
                            });
                        } catch (Exception ex) {
                            ViewUtils.showErrorMsg("ERROR", ex);
                            return;
                        }
                    });
                    dltBtn.getStyle().set("color", "red");

                    Button uptBtn = new Button(
                            new Icon(VaadinIcon.PENCIL),
                            buttonClickEvent -> new UpdateCardStatusView(this, cardStatus)
                    );
                    return new HorizontalLayout(uptBtn, dltBtn);
                })
                .setTextAlign(ColumnTextAlign.END);

        cardStatusList.setItems(getService().findByWithTechIdIsNull());
    }

    public void reloadData() {
        parentView.reloadData();
        dialog.removeAll();
        try {
            initProducts();
        } catch (Exception ex) {
            ViewUtils.showErrorMsg("ERROR", ex);
            crtBtn.setEnabled(true);
            return;
        }
        dialog.setMaxHeight(50, Unit.PERCENTAGE);
        dialog.setMaxWidth(50, Unit.PERCENTAGE);
        dialog.setSizeFull();

        Div dialogComponents = new Div(
                new Label("Card status list"),
                cardStatusList,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(dialog))
        );
        dialogComponents.setSizeFull();
        dialog.add(dialogComponents);
    }

    public CardStatusService getService() {
        return mainView.getCardStatusService();
    }
}
