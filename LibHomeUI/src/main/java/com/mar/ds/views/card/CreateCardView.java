package com.mar.ds.views.card;

import com.mar.ds.utils.FileUtils;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardStatusDto;
import com.mar.libhome.dto.CardTypeDto;
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
import static java.util.Objects.nonNull;

@Slf4j
public class CreateCardView extends CardDialogView {

    private final Dialog createDialog;

    public CreateCardView(MainView mainView, FileUtils.ViewTypeDto viewType) {
        this.mainView = mainView;
        this.viewType = viewType;
        this.minPoint = Integer.parseInt(mainView.getEnv().getProperty("app.card.point.min", "0"));
        this.maxPoint = Integer.parseInt(mainView.getEnv().getProperty("app.card.point.max", "10"));

        createDialog = new Dialog();
        createDialog.add(new Label("Create"));
        createDialog.setMaxHeight(80, Unit.PERCENTAGE);
        createDialog.setMaxWidth(80, Unit.PERCENTAGE);
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        // title
        createDialog.add(getTitle());
        // point
        // engine
        if (nonNull(getTitles().get(GRID_POINT)) && nonNull(getTitles().get(GRID_ENGINE))) {
            createDialog.add(new HorizontalLayout(getPointField(), getEngineSelector()));
        } else {
            if (nonNull(getTitles().get(GRID_POINT))) {
                createDialog.add(getPointField());
            }
            if (nonNull(getTitles().get(GRID_ENGINE))) {
                createDialog.add(getEngineSelector());
            }
        }
        // type view
        createDialog.add(getViewTypeSelector(viewType));
        // link
        if (nonNull(getTitles().get(GRID_LINK))) {
            createDialog.add(getLinkFiled());
        }
        // lang
        if (nonNull(getTitles().get(GRID_LANGUAGE))) {
            createDialog.add(getLanguageSelector());
        }
        // last update
        // last game
        if (nonNull(getTitles().get(GRID_DATE_UPD)) && nonNull(getTitles().get(GRID_DATE_GAME))) {
            createDialog.add(new HorizontalLayout(getUpdDate(), getGameDate()));
        } else {
            if (nonNull(getTitles().get(GRID_DATE_UPD))) {
                createDialog.add(getUpdDate());
            }
            if (nonNull(getTitles().get(GRID_DATE_GAME))) {
                createDialog.add(getGameDate());
            }
        }

        // status, type + tags
        List<Component> components = new LinkedList<>();
        if (nonNull(getTitles().get(GRID_STATUS))) {
            components.add(getStatusSelector());
        }
        if (nonNull(getTitles().get(GRID_TYPE)) && nonNull(getTitles().get(GRID_TAGS))) {
            components.add(getTypeSelector());
            components.add(getTagMultiselector());
        }
        if (components.size() > 1) {
            createDialog.add(new HorizontalLayout(components.toArray(new Component[0])));
        } else {
            if (!components.isEmpty()) {
                createDialog.add(components.get(0));
            }
        }
        // info
        if (nonNull(getTitles().get(GRID_INFO))) {
            createDialog.add(getInfo());
        }

        Button crtBtn = new Button("Create", new Icon(VaadinIcon.PLUS));
        crtBtn.addClickListener(click -> {
            try {
                checkValues();
                CardStatusDto cardStatus = Optional.ofNullable(cardStatusListSelect.getValue()).orElseThrow(() -> new RuntimeException("Card STATUS is empty."));
                CardTypeDto cardType = Optional.ofNullable(cardTypeListSelect.getValue()).orElseThrow(() -> new RuntimeException("Card TYPE is empty."));
                CardDto card = mainView.getCardService().save(
                        CardDto.builder()
                                .title(getTextFieldValue(cardTitle).orElseThrow(() -> new RuntimeException("Card TITLE is EMPTY.")))
                                .viewType(getValue(viewTypeDtoSelect, viewType).id())
                                .info(Optional.ofNullable(getTextFieldValue(infoArea)).orElse(""))
                                .link(getTextFieldValue(link).orElse(null))
                                .engine(getValue(engineSelect, GameEngine.DEFAULT))
                                .point(getDoubleValue(point))
                                .lastUpdate(getValue(updDate, new Date()))
                                .lastGame(getValue(gameDate, new Date()))
                                .cardStatus(SerializationUtils.clone(cardStatus))
                                .cardType(cardType)
                                .tagList(tags.getValue().stream().toList())
                                .language(getValue(languageSelect, Language.DEFAULT))
                                .build()
                );
                mainView.getCardHistoryService().saveCreateCard(card);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("An error occurred while creating", ex);
                crtBtn.setEnabled(true);
                return;
            }
            mainView.getActiveView().reloadData();
            createDialog.close();
        });
        crtBtn.setWidthFull();
        crtBtn.setDisableOnClick(true);

        createDialog.add(new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(createDialog)));
    }

    @Override
    public void showDialog() {
        createDialog.open();
    }

    @Override
    public void closeDialog() {
        createDialog.close();
    }
}
