package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.CardType;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.db.entity.GameEngine;
import com.mar.ds.db.entity.Language;
import com.mar.ds.db.entity.ViewType;
import com.mar.ds.utils.FileUtils;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Component;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import static com.mar.ds.data.GridInfo.GRID_TITLE;
import static com.mar.ds.data.GridInfo.GRID_TYPE;
import static com.mar.ds.utils.ViewUtils.getDoubleValue;
import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static com.mar.ds.utils.ViewUtils.getValue;
import static java.util.Objects.nonNull;

@Slf4j
public class CreateCardView extends CardDialogView {

    private final Dialog createDialog;

    public CreateCardView(MainView mainView, ViewType viewType) {
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
                mainView.getRepositoryService().getCardRepository()
                        .save(
                                Card.builder()
                                        .viewType(viewType)
                                        .title(getTextFieldValue(cardTitle))
                                        .info(Optional.ofNullable(getTextFieldValue(infoArea)).orElse(""))
                                        .link(getTextFieldValue(link))
                                        .engine(getValue(engineSelect, GameEngine.DEFAULT))
                                        .point(getDoubleValue(point))
                                        .lastUpdate(getValue(updDate, new Date()))
                                        .lastGame(getValue(gameDate, new Date()))
                                        .cardStatus(cardStatusListSelect.getValue())
                                        .cardType(cardTypeListSelect.getValue())
                                        .tagList(tags.getValue().stream().toList())
                                        .language(getValue(languageSelect, Language.DEFAULT))
                                        .build()
                        );
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("An error occurred while creating", ex);
                crtBtn.setEnabled(true);
                return;
            }
            mainView.reloadContent();
            createDialog.close();
        });
        crtBtn.setWidthFull();
        crtBtn.setDisableOnClick(true);
        crtBtn.addClickShortcut(Key.ENTER);

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
