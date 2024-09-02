package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.CardType;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
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
import org.vaadin.gatanaso.MultiselectComboBox;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.mar.ds.utils.ViewUtils.getDoubleValue;
import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static com.mar.ds.utils.ViewUtils.setBigDecimalFieldValue;
import static com.mar.ds.utils.ViewUtils.setMultiSelectComboBoxValue;
import static com.mar.ds.utils.ViewUtils.setSelectValue;
import static com.mar.ds.utils.ViewUtils.setTextFieldValue;

@Slf4j
public class UpdateCardView {

    public UpdateCardView(MainView mainView, Card updateCard) {
        Dialog updateDialog = new Dialog();
        updateDialog.setMaxHeight(80, Unit.PERCENTAGE);
        updateDialog.setMaxWidth(80, Unit.PERCENTAGE);
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);

        // title
        TextField cardTitle = new TextField("Title");
        cardTitle.setRequired(true);
        cardTitle.setWidthFull();
        setTextFieldValue(cardTitle, updateCard.getTitle());
        // text
        TextArea infoArea = new TextArea("Info");
        infoArea.setRequired(true);
        infoArea.setWidthFull();
        setTextFieldValue(infoArea, updateCard.getInfo());
        // point
        BigDecimalField point = new BigDecimalField("Point");
        point.setWidthFull();
        setBigDecimalFieldValue(point, updateCard.getPoint());
        // last update
        DatePicker lastUpdDate = new DatePicker("Last update", LocalDate.now());
        lastUpdDate.setWidthFull();
        lastUpdDate.setRequired(true);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(updateCard.getLastUpdate());
        lastUpdDate.setValue(LocalDate.of(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)
        ));
        // last game
        DatePicker lastGameDate = new DatePicker("Last game", LocalDate.now());
        lastGameDate.setWidthFull();
        lastGameDate.setRequired(true);
        calendar.setTime(updateCard.getLastGame());
        lastGameDate.setValue(LocalDate.of(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)
        ));
        // status
        List<CardStatus> cardStatusList = mainView.getRepositoryService().getCardStatusRepository().findAll();
        Select<CardStatus> cardStatusListSelect = new Select<>();
        cardStatusListSelect.setLabel("Status");
        cardStatusListSelect.setEmptySelectionAllowed(false);
        cardStatusListSelect.setTextRenderer(CardStatus::getTitle);
        cardStatusListSelect.setDataProvider(new ListDataProvider<>(cardStatusList));
        cardStatusListSelect.setWidthFull();
        ViewUtils.setSelectValue(cardStatusListSelect, updateCard.getCardStatus(), cardStatusList);
        // type
        List<CardType> cardTypeList = mainView.getRepositoryService().getCardTypeRepository().findAll();
        Select<CardType> cardTypeListSelect = new Select<>();
        cardTypeListSelect.setLabel("Type");
        cardTypeListSelect.setEmptySelectionAllowed(false);
        cardTypeListSelect.setTextRenderer(CardType::getTitle);
        cardTypeListSelect.setDataProvider(new ListDataProvider<>(cardTypeList));
        cardTypeListSelect.setWidthFull();
        setSelectValue(cardTypeListSelect, updateCard.getCardType(), cardTypeList);
        // type tags
        MultiselectComboBox<CardTypeTag> tags = new MultiselectComboBox<>();
        tags.setLabel("Tags");
        tags.setPlaceholder("Select tags...");
        tags.setItemLabelGenerator(CardTypeTag::getTitle);
        tags.setWidthFull();
        tags.setAllowCustomValues(false);
        tags.setClearButtonVisible(true);
        cardTypeListSelect.addValueChangeListener(event -> {
            tags.deselectAll();
            List<CardTypeTag> tagList = mainView.getRepositoryService().getCardTypeTagRepository().findByCardType(event.getValue());
            tags.setItems(tagList);
        });
        List<CardTypeTag> tagList = mainView.getRepositoryService().getCardTypeTagRepository().findByCardType(updateCard.getCardType());
        setMultiSelectComboBoxValue(tags, tagList, updateCard.getTagList());
        // link
        TextField link = new TextField("Link");
        link.setRequired(true);
        link.setWidthFull();
        setTextFieldValue(link, updateCard.getLink());

        Button updBtn = new Button("Update", new Icon(VaadinIcon.ROTATE_RIGHT));
        updBtn.addClickListener(click -> {
            try {
                updateCard.setTitle(getTextFieldValue(cardTitle));
                updateCard.setInfo(getTextFieldValue(infoArea));
                updateCard.setLink(getTextFieldValue(link));
                updateCard.setCardStatus(cardStatusListSelect.getValue());
                updateCard.setCardType(cardTypeListSelect.getValue());
                updateCard.setPoint(getDoubleValue(point));
                updateCard.setLastUpdate(Date.from(lastUpdDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                updateCard.setLastGame(Date.from(lastGameDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                updateCard.setTagList(tags.getValue().stream().toList());
                mainView.getRepositoryService().getCardRepository().save(updateCard);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("ERROR", ex);
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
                new Label("Update"),
                cardTitle,
                point,
                link,
                lastUpdDate,
                lastGameDate,
                cardStatusListSelect,
                cardTypeListSelect,
                tags,
                infoArea,
                new HorizontalLayout(updBtn, ViewUtils.getCloseButton(updateDialog))
        );
        updateDialog.open();
    }

}
