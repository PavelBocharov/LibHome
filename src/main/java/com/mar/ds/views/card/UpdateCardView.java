package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.CardType;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.List;

import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static com.mar.ds.utils.ViewUtils.setTextFieldValue;
import static java.lang.String.format;

public class UpdateCardView {

    public UpdateCardView(MainView mainView, Card updateCard) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);

        // title
        TextField cardTitle = new TextField("Заголовок");
        cardTitle.setRequired(true);
        cardTitle.setWidthFull();
        setTextFieldValue(cardTitle, updateCard.getTitle());
        // text
        TextArea infoArea = new TextArea("Текст");
        infoArea.setRequired(true);
        infoArea.setWidthFull();
        setTextFieldValue(infoArea, updateCard.getInfo());
        // title
        TextField link = new TextField("Link");
        link.setRequired(true);
        link.setWidthFull();
        setTextFieldValue(link, updateCard.getLink());
        // status
        List<CardStatus> cardStatusList = mainView.getRepositoryService().getCardStatusRepository().findAll();
        Select<CardStatus> cardStatusListSelect = new Select<>();
        cardStatusListSelect.setLabel("Статус");
        cardStatusListSelect.setEmptySelectionAllowed(false);
        cardStatusListSelect.setTextRenderer(documentStatus -> format("%32s", documentStatus.getTitle()));
        cardStatusListSelect.setDataProvider(new ListDataProvider<>(cardStatusList));
        cardStatusListSelect.setWidthFull();
        ViewUtils.setSelectValue(cardStatusListSelect, updateCard.getCardStatus(), cardStatusList);
        // type
        List<CardType> cardTypeList = mainView.getRepositoryService().getCardTypeRepository().findAll();
        Select<CardType> cardTypeListSelect = new Select<>();
        cardTypeListSelect.setLabel("Тип");
        cardTypeListSelect.setEmptySelectionAllowed(false);
        cardTypeListSelect.setTextRenderer(documentStatus -> documentStatus.getTitle());
        cardTypeListSelect.setDataProvider(new ListDataProvider<>(cardTypeList));
        cardTypeListSelect.setWidthFull();
        ViewUtils.setSelectValue(cardTypeListSelect, updateCard.getCardType(), cardTypeList);

        Button updBtn = new Button("Обновить", new Icon(VaadinIcon.ROTATE_RIGHT));
        updBtn.addClickListener(click -> {
            try {
                updateCard.setTitle(getTextFieldValue(cardTitle));
                updateCard.setInfo(getTextFieldValue(infoArea));
                updateCard.setLink(getTextFieldValue(link));
                updateCard.setCardStatus(cardStatusListSelect.getValue());
                updateCard.setCardType(cardTypeListSelect.getValue());

                mainView.getRepositoryService().getCardRepository().save(updateCard);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                updBtn.setEnabled(true);
                return;
            }
            mainView.setContent(mainView.getCardView().getContent());
            updateDialog.close();
        });
        updBtn.setWidthFull();
        updBtn.setDisableOnClick(true);
        updBtn.addClickShortcut(Key.ENTER);

        updateDialog.add(
                new Label("Обновить документ"),
                cardTitle,
                infoArea,
                link,
                cardStatusListSelect,
                cardTypeListSelect,
                new HorizontalLayout(updBtn, ViewUtils.getCloseButton(updateDialog))
        );
        updateDialog.open();
    }

}
