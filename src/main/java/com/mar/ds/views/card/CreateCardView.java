package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.CardType;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.mar.ds.utils.ViewUtils.getDoubleValue;
import static com.mar.ds.utils.ViewUtils.getTextFieldValue;

@Slf4j
public class CreateCardView {

    public CreateCardView(MainView mainView) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        // title
        TextField cardTitle = new TextField("Title");
        cardTitle.setRequired(true);
        cardTitle.setWidthFull();
        // text
        TextArea infoArea = new TextArea("Info");
        infoArea.setRequired(true);
        infoArea.setWidthFull();
        // point
        BigDecimalField point = new BigDecimalField("Point");
        point.setWidthFull();
        // last update
        DatePicker lastUpdDate = new DatePicker("Last update", LocalDate.now());
        lastUpdDate.setWidthFull();
        lastUpdDate.setRequired(true);
        // last game
        DatePicker lastGameDate = new DatePicker("Last game", LocalDate.now());
        lastGameDate.setWidthFull();
        lastGameDate.setRequired(true);
        // status
        List<CardStatus> cardStatusList = mainView.getRepositoryService().getCardStatusRepository().findAll();
        Select<CardStatus> cardStatusListSelect = new Select<>();
        cardStatusListSelect.setLabel("Status");
        cardStatusListSelect.setEmptySelectionAllowed(false);
        cardStatusListSelect.setTextRenderer(CardStatus::getTitle);
        cardStatusListSelect.setDataProvider(new ListDataProvider<>(cardStatusList));
        cardStatusListSelect.setWidthFull();
        // type
        List<CardType> cardTypeList = mainView.getRepositoryService().getCardTypeRepository().findAll();
        Select<CardType> cardTypeListSelect = new Select<>();
        cardTypeListSelect.setLabel("Type");
        cardTypeListSelect.setEmptySelectionAllowed(false);
        cardTypeListSelect.setTextRenderer(CardType::getTitle);
        cardTypeListSelect.setDataProvider(new ListDataProvider<>(cardTypeList));
        cardTypeListSelect.setWidthFull();
        // link
        TextField link = new TextField("Link");
        link.setRequired(true);
        link.setWidthFull();

        Button crtBtn = new Button("Create", new Icon(VaadinIcon.PLUS));
        crtBtn.addClickListener(click -> {
            try {
                Card card = mainView.getRepositoryService().getCardRepository()
                        .save(
                                Card.builder()
                                        .title(getTextFieldValue(cardTitle))
                                        .info(getTextFieldValue(infoArea))
                                        .link(getTextFieldValue(link))
                                        .point(getDoubleValue(point))
                                        .previewImage("")
                                        .cardStatus(cardStatusListSelect.getValue())
                                        .cardType(cardTypeListSelect.getValue())
                                        .lastUpdate(Date.from(lastUpdDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                                        .lastGame(Date.from(lastGameDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                                        .build()
                        );
                log.debug("Create card: {}", card);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("An error occurred while creating", ex);
                crtBtn.setEnabled(true);
                return;
            }
            mainView.setContent(mainView.getCardView().getContent());
            createDialog.close();
        });
        crtBtn.setWidthFull();
        crtBtn.setDisableOnClick(true);
        crtBtn.addClickShortcut(Key.ENTER);

        createDialog.add(
                new Label("Create card"),
                cardTitle,
                point,
                link,
                lastUpdDate,
                lastGameDate,
                cardStatusListSelect,
                cardTypeListSelect,
                infoArea,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(createDialog))
        );
        createDialog.open();
    }

}
