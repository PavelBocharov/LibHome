package com.mar.ds.views.card;

import com.mar.ds.utils.FileUtils;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardStatusDto;
import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.dto.CardTypeTagDto;
import com.mar.libhome.enums.GameEngine;
import com.mar.libhome.enums.Language;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

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

    public UpdateCardView(MainView mainView, CardDto updateCard, FileUtils.ViewTypeDto viewType, Runnable afterUpdateEvent) {
        this.mainView = mainView;
        this.viewType = viewType;
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
        // type view
        updateDialog.add(getViewTypeSelector(viewType));
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
            List<CardStatusDto> cardStatusList = mainView.getCardStatusService().findAll();
            setSelectValue(
                    cardStatusListSelect,
                    CardStatusDto.builder().id(updateCard.getCardStatus().getId()).build(),
                    cardStatusList
            );
        }
        if (nonNull(getTitles().get(GRID_TYPE)) && nonNull(getTitles().get(GRID_TAGS))) {
            // type
            components.add(getTypeSelector());
            List<CardTypeDto> cardTypeList = mainView.getCardTypeService().findAll();
            setSelectValue(cardTypeListSelect, updateCard.getCardType(), cardTypeList);
            // type tags
            components.add(getTagMultiselector());
            List<CardTypeTagDto> tagList = mainView
                    .getCardTypeTagService()
                    .findByCardType(updateCard.getCardType());
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
                CardDto old = SerializationUtils.clone(updateCard);
                CardStatusDto cardStatus = Optional.ofNullable(cardStatusListSelect.getValue()).orElseThrow(() -> new RuntimeException("Card STATUS is empty."));
                CardTypeDto cardType = Optional.ofNullable(cardTypeListSelect.getValue()).orElseThrow(() -> new RuntimeException("Card TYPE is empty."));
                updateCard.setTitle(getTextFieldValue(cardTitle).orElseThrow(() -> new RuntimeException("Card status TITLE is EMPTY.")));
                updateCard.setViewType(getValue(viewTypeDtoSelect, viewType).id());
                updateCard.setInfo(Optional.ofNullable(getTextFieldValue(infoArea)).orElse(""));
                updateCard.setLink(getTextFieldValue(link).orElse(null));
                updateCard.setEngine(getValue(engineSelect, GameEngine.DEFAULT));
                updateCard.setCardStatus(SerializationUtils.clone(cardStatus));
                updateCard.setCardType(cardType);
                updateCard.setPoint(getDoubleValue(point));
                updateCard.setLastUpdate(getValue(updDate, new Date()));
                updateCard.setLastGame(getValue(gameDate, new Date()));
                updateCard.setTagList(tags.getValue().stream().toList());
                updateCard.setLanguage(getValue(languageSelect, Language.DEFAULT));

                mainView.getCardHistoryService().saveHistory(
                        old,
                        mainView.getCardService().save(updateCard)
                );
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
