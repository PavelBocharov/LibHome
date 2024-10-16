package com.mar.ds.views.card.status;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.jpa.CardStatusRepository;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
public class CardStatusViewDialog {
    private final MainView appLayout;
    private Dialog dialog;
    private VerticalLayout cardStatusList;
    private Button crtBtn;

    public CardStatusViewDialog(MainView appLayout) {
        this.appLayout = appLayout;

        dialog = new Dialog();

        crtBtn = new Button("Create card status", new Icon(VaadinIcon.PLUS));
        crtBtn.setWidthFull();
        crtBtn.addClickListener(btnClick -> new CreateCardStatusView(this));

        reloadData();

        dialog.open();
    }


    private void initProducts() {
        cardStatusList = new VerticalLayout();

        List<CardStatus> cardStatusList = getRepository().findAll();

        for (CardStatus cardStatus : cardStatusList) {
            TextField name = new TextField();
            name.setTitle("Name");
            name.setEnabled(false);
            name.setWidthFull();
            name.setValue(cardStatus.getTitle());

            Button dltBtn = new Button(new Icon(VaadinIcon.BAN), buttonClickEvent -> {
                try {
                    new DeleteDialogWidget(() -> {
                        List<Card> cards = appLayout.getRepositoryService().getCardRepository().findByCardStatus(cardStatus);
                        if (isEmpty(cards)) {
                            log.info("Not find cards by status: {}. Delete status.", cardStatus);
                            getRepository().delete(cardStatus);
                            reloadData();
                        } else {
                            log.warn("Find cards by status: {}, list: {}", cardStatus, cards);
                            ViewUtils.showErrorMsg(
                                    "Delete card status ERROR",
                                    new Exception(String.format("Find cards with status: '%s', count: %d.", cardStatus.getTitle(), cards.size()))
                            );
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
            this.cardStatusList.add(new HorizontalLayout(name, uptBtn, dltBtn));
        }
    }

    public void reloadData() {
        try {
            initProducts();
        } catch (Exception ex) {
            ViewUtils.showErrorMsg("ERROR", ex);
            crtBtn.setEnabled(true);
            return;
        }
        appLayout.reloadContent();
        dialog.removeAll();
        dialog.add(
                new Label("Card status list"),
                cardStatusList,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(dialog))
        );
    }

    public CardStatusRepository getRepository() {
        return appLayout.getRepositoryService().getCardStatusRepository();
    }
}
