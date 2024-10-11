package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.CardType;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.db.entity.GameEngine;
import com.mar.ds.db.entity.Language;
import com.mar.ds.utils.FileUtils;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.mar.ds.data.GridInfo.GRID_DATE_GAME;
import static com.mar.ds.data.GridInfo.GRID_DATE_UPD;
import static com.mar.ds.data.GridInfo.GRID_ENGINE;
import static com.mar.ds.data.GridInfo.GRID_INFO;
import static com.mar.ds.data.GridInfo.GRID_LANGUAGE;
import static com.mar.ds.data.GridInfo.GRID_LINK;
import static com.mar.ds.data.GridInfo.GRID_POINT;
import static com.mar.ds.data.GridInfo.GRID_STATUS;
import static com.mar.ds.data.GridInfo.GRID_TAGS;
import static com.mar.ds.data.GridInfo.GRID_TYPE;
import static com.mar.ds.utils.ViewUtils.getDoubleValue;
import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static com.mar.ds.utils.ViewUtils.getValue;
import static com.mar.ds.utils.ViewUtils.setBigDecimalFieldValue;
import static com.mar.ds.utils.ViewUtils.setMultiSelectComboBoxValue;
import static com.mar.ds.utils.ViewUtils.setSelectValue;
import static com.mar.ds.utils.ViewUtils.setTextFieldValue;
import static com.mar.ds.utils.ViewUtils.setValue;
import static java.util.Objects.nonNull;

@Slf4j
public class UpdateCardView extends CardDialogView {

    private final Dialog updateDialog;

    public UpdateCardView(MainView mainView, Card updateCard, Runnable afterUpdateEvent) {
        this.mainView = mainView;
        this.viewType = updateCard.getViewType();
        this.minPoint = Integer.parseInt(mainView.getEnv().getProperty("app.card.point.min", "0"));
        this.maxPoint = Integer.parseInt(mainView.getEnv().getProperty("app.card.point.max", "10"));

        updateDialog = new Dialog();
        updateDialog.add(new Label("Update"));
        updateDialog.setMaxHeight(80, Unit.PERCENTAGE);
        updateDialog.setMaxWidth(80, Unit.PERCENTAGE);
        updateDialog.setCloseOnEsc(false);
        updateDialog.setCloseOnOutsideClick(false);

        // title
        updateDialog.add(getTitle());
        setTextFieldValue(cardTitle, updateCard.getTitle());
        // point
        // engine
        if (nonNull(getTitles().get(GRID_POINT)) && nonNull(getTitles().get(GRID_ENGINE))) {
            updateDialog.add(new HorizontalLayout(getPointField(), getEngineSelector()));
            setBigDecimalFieldValue(point, updateCard.getPoint());
            setSelectValue(engineSelect, updateCard.getEngine(), GameEngine.values());
        } else {
            if (nonNull(getTitles().get(GRID_POINT))) {
                updateDialog.add(getPointField());
                setBigDecimalFieldValue(point, updateCard.getPoint());
            }
            if (nonNull(getTitles().get(GRID_ENGINE))) {
                updateDialog.add(getEngineSelector());
                setSelectValue(engineSelect, updateCard.getEngine(), GameEngine.values());
            }
        }
        // link
        if (nonNull(getTitles().get(GRID_LINK))) {
            updateDialog.add(getLinkFiled());
            setTextFieldValue(link, updateCard.getLink());
        }
        //lang
        if (nonNull(getTitles().get(GRID_LANGUAGE))) {
            updateDialog.add(getLanguageSelector());
            setSelectValue(languageSelect, updateCard.getLanguage(), Language.values(), Language.DEFAULT);
        }
        // last update
        // last game
        if (nonNull(getTitles().get(GRID_DATE_UPD)) && nonNull(getTitles().get(GRID_DATE_GAME))) {
            updateDialog.add(new HorizontalLayout(getUpdDate(), getGameDate()));
            setValue(updDate, updateCard.getLastUpdate());
            setValue(gameDate, updateCard.getLastGame());
        } else {
            if (nonNull(getTitles().get(GRID_DATE_UPD))) {
                updateDialog.add(getUpdDate());
                setValue(updDate, updateCard.getLastUpdate());
            }
            if (nonNull(getTitles().get(GRID_DATE_GAME))) {
                updateDialog.add(getGameDate());
                setValue(gameDate, updateCard.getLastGame());
            }
        }

        // status, type + tags
        List<Component> components = new LinkedList<>();
        if (nonNull(getTitles().get(GRID_STATUS))) {
            components.add(getStatusSelector());
            List<CardStatus> cardStatusList = mainView.getRepositoryService().getCardStatusRepository().findAll();
            setSelectValue(cardStatusListSelect, updateCard.getCardStatus(), cardStatusList);
        }
        if (nonNull(getTitles().get(GRID_TYPE)) && nonNull(getTitles().get(GRID_TAGS))) {
            // type
            components.add(getTypeSelector());
            List<CardType> cardTypeList = mainView.getRepositoryService().getCardTypeRepository().findAll();
            setSelectValue(cardTypeListSelect, updateCard.getCardType(), cardTypeList);
            // type tags
            components.add(getTagMultiselector());
            List<CardTypeTag> tagList = mainView.getRepositoryService().getCardTypeTagRepository().findByCardType(updateCard.getCardType());
            setMultiSelectComboBoxValue(tags, tagList, updateCard.getTagList());
        }
        if (components.size() > 1) {
            updateDialog.add(new HorizontalLayout(components.toArray(new Component[0])));
        } else {
            if (!components.isEmpty()) {
                updateDialog.add(components.get(0));
            }
        }
        // info
        if (nonNull(getTitles().get(GRID_INFO))) {
            updateDialog.add(getInfo());
            setTextFieldValue(infoArea, updateCard.getInfo());
        }

        Button updBtn = new Button("Update", new Icon(VaadinIcon.ROTATE_RIGHT));
        updBtn.addClickListener(click -> {
            try {
                checkValues();
                updateCard.setTitle(getTextFieldValue(cardTitle));
                updateCard.setInfo(Optional.ofNullable(getTextFieldValue(infoArea)).orElse(""));
                updateCard.setLink(getTextFieldValue(link));
                updateCard.setEngine(getValue(engineSelect, GameEngine.DEFAULT));
                updateCard.setCardStatus(cardStatusListSelect.getValue());
                updateCard.setCardType(cardTypeListSelect.getValue());
                updateCard.setPoint(getDoubleValue(point));
                updateCard.setLastUpdate(getValue(updDate, new Date()));
                updateCard.setLastGame(getValue(gameDate, new Date()));
                updateCard.setTagList(tags.getValue().stream().toList());
                updateCard.setLanguage(getValue(languageSelect, Language.DEFAULT));
                mainView.getRepositoryService().getCardRepository().save(updateCard);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("ERROR", ex);
                updBtn.setEnabled(true);
                return;
            }
            afterUpdateEvent.run();
            updateDialog.close();
        });
        updBtn.setWidthFull();
        updBtn.setDisableOnClick(true);
        updBtn.addClickShortcut(Key.ENTER);

        updateDialog.add(new HorizontalLayout(updBtn, ViewUtils.getCloseButton(updateDialog)));
    }

    @Override
    public void showDialog() {
        updateDialog.open();
    }

    @Override
    public void closeDialog() {
        updateDialog.close();
    }

}
