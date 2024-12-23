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
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;

import static java.lang.Boolean.TRUE;

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

        cardStatusList.addColumn(CardStatus::getTitle)
                .setHeader("Title")
                .setSortable(true);
        cardStatusList.addColumn(CardStatus::getOrder)
                .setHeader("Order")
                .setSortable(true);
        cardStatusList
                .addComponentColumn(cardStatus ->
                        TRUE.equals(cardStatus.getIsRate()) ? trueIcon() : falseIcon()
                )
                .setHeader("Is rate")
                .setSortable(true)
                .setComparator(CardStatus::getIsRate);
        cardStatusList
                .addComponentColumn(cardStatus ->
                        TRUE.equals(cardStatus.getHasUpdStatus()) ? trueIcon() : falseIcon()
                )
                .setHeader("Has UPD")
                .setSortable(true)
                .setComparator(CardStatus::getHasUpdStatus);
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
        HorizontalLayout btns = new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(dialog));
        VerticalLayout verticalLayout = new VerticalLayout(
                new H3("Card status list"),
                cardStatusList,
                btns
        );
        verticalLayout.setSizeFull();
        verticalLayout.getStyle().set("padding", "0px");
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, cardStatusList);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, btns);
        dialog.add(verticalLayout);
    }

    private Icon trueIcon() {
        Icon icon = VaadinIcon.PLUS_SQUARE_O.create();
        icon.setColor("green");
        return icon;
    }

    private Icon falseIcon() {
        Icon icon = VaadinIcon.MINUS_SQUARE_O.create();
        icon.setColor("red");
        return icon;
    }

    public CardStatusService getService() {
        return mainView.getCardStatusService();
    }
}
