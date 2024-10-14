package com.mar.ds.views.card;

import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.CardType;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.db.entity.GameEngine;
import com.mar.ds.db.entity.Language;
import com.mar.ds.db.entity.ViewType;
import com.mar.ds.utils.FileUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mar.ds.data.GridInfo.GRID_DATE_GAME;
import static com.mar.ds.data.GridInfo.GRID_DATE_UPD;
import static com.mar.ds.data.GridInfo.GRID_ENGINE;
import static com.mar.ds.data.GridInfo.GRID_INFO;
import static com.mar.ds.data.GridInfo.GRID_LINK;
import static com.mar.ds.data.GridInfo.GRID_POINT;
import static com.mar.ds.data.GridInfo.GRID_STATUS;
import static com.mar.ds.data.GridInfo.GRID_TAGS;
import static com.mar.ds.data.GridInfo.GRID_TITLE;
import static com.mar.ds.data.GridInfo.GRID_TYPE;
import static com.mar.ds.utils.ViewUtils.getDoubleValue;
import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.join;

public abstract class CardDialogView {

    private volatile Map<String, String> titles;

    protected MainView mainView;
    protected ViewType viewType;
    protected int minPoint;
    protected int maxPoint;

    protected TextField cardTitle;
    protected BigDecimalField point;
    protected Select<GameEngine> engineSelect;
    protected TextField link;
    protected DatePicker updDate;
    protected DatePicker gameDate;
    protected Select<CardStatus> cardStatusListSelect;
    protected Select<CardType> cardTypeListSelect;
    protected MultiselectComboBox<CardTypeTag> tags;
    protected TextArea infoArea;
    protected Select<Language> languageSelect;

    public abstract void showDialog();
    public abstract void closeDialog();

    protected Map<String, String> getTitles() {
        if (isNull(titles)) {
            synchronized (this) {
                if (isNull(titles)) {
                    titles = FileUtils.getTitles(viewType, mainView.getContentJSON());
                }
            }
        }
        return titles;
    }

    protected void checkValues() throws Exception {
        if (isBlank(getTextFieldValue(cardTitle))) {
            throw new RuntimeException(String.format("'%s' not be blank.", getTitles().get(GRID_TITLE)));
        }
        if (nonNull(point)) {
            double cardPoint = getDoubleValue(point);
            if (minPoint > cardPoint || maxPoint < cardPoint) {
                point.setInvalid(true);
                throw new Exception(String.format("Point value ERROR.\nMin value = %d, max value = %d", minPoint, maxPoint));
            } else {
                point.setInvalid(false);
            }
        }
    }

    protected Component getTitle() {
        cardTitle = new TextField(
                Optional.of(getTitles().get(GRID_TITLE)).orElseThrow(() -> new RuntimeException(
                                String.format("Not init '%s' label text.", GRID_TITLE)
                        )
                )
        );
        cardTitle.setRequired(true);
        cardTitle.setWidthFull();
        return cardTitle;
    }

    protected Component getPointField() {
        point = new BigDecimalField(getTitles().get(GRID_POINT));
        point.setValue(BigDecimal.valueOf(minPoint));
        point.setHelperText(String.format("Min value = %d, max value = %d", minPoint, maxPoint));
        point.setWidthFull();
        return point;
    }

    protected Component getEngineSelector() {
        engineSelect = new Select<>(GameEngine.values());
        engineSelect.setLabel(getTitles().get(GRID_ENGINE));
        engineSelect.setEmptySelectionAllowed(false);
        engineSelect.setTextRenderer(GameEngine::getName);
        engineSelect.setWidthFull();
        engineSelect.setValue(GameEngine.RENPY);
        return engineSelect;
    }

    protected Component getLinkFiled() {
        link = new TextField(getTitles().get(GRID_LINK));
        link.setWidthFull();
        return link;
    }

    protected Component getUpdDate() {
        updDate = new DatePicker(getTitles().get(GRID_DATE_UPD), LocalDate.now());
        updDate.setWidthFull();
        return updDate;
    }

    protected Component getGameDate() {
        gameDate = new DatePicker(getTitles().get(GRID_DATE_GAME), LocalDate.now());
        gameDate.setWidthFull();
        return gameDate;
    }

    protected Component getStatusSelector() {
        List<CardStatus> cardStatusList = mainView.getRepositoryService().getCardStatusRepository().findAll();
        cardStatusListSelect = new Select<>();
        cardStatusListSelect.setLabel(getTitles().get(GRID_STATUS));
        cardStatusListSelect.setEmptySelectionAllowed(false);
        cardStatusListSelect.setTextRenderer(CardStatus::getTitle);
        cardStatusListSelect.setDataProvider(new ListDataProvider<>(cardStatusList));
        cardStatusListSelect.setWidthFull();
        return cardStatusListSelect;
    }

    protected Component getTypeSelector() {
        List<CardType> cardTypeList = mainView.getRepositoryService().getCardTypeRepository().findAll();
        cardTypeListSelect = new Select<>();
        cardTypeListSelect.setLabel(getTitles().get(GRID_TYPE));
        cardTypeListSelect.setEmptySelectionAllowed(false);
        cardTypeListSelect.setTextRenderer(CardType::getTitle);
        cardTypeListSelect.setDataProvider(new ListDataProvider<>(cardTypeList));
        cardTypeListSelect.setWidthFull();
        return cardTypeListSelect;
    }

    protected Component getTagMultiselector() {
        tags = new MultiselectComboBox<>();
        tags.setLabel(getTitles().get(GRID_TAGS));
        tags.setItemLabelGenerator(CardTypeTag::getTitle);
        tags.setWidthFull();
        tags.setAllowCustomValues(false);
        tags.setClearButtonVisible(true);
        cardTypeListSelect.addValueChangeListener(event -> {
            tags.deselectAll();
            if (nonNull(event.getValue())) {
                List<CardTypeTag> tagList = mainView
                        .getRepositoryService()
                        .getCardTypeTagRepository()
                        .findByCardType(event.getValue());
                tags.setItems(tagList);
            } else {
                tags.setItems(Collections.emptyList());
                tags.clear();
            }
        });
        return tags;
    }

    protected Component getInfo() {
        infoArea = new TextArea(getTitles().get(GRID_INFO));
        infoArea.setWidthFull();
        return infoArea;
    }

    protected Component getLanguageSelector() {
        languageSelect = new Select<>(Language.values());
        languageSelect.setLabel("Language/Язык");
        languageSelect.setEmptySelectionAllowed(false);
        languageSelect.setTextRenderer(language -> join(language.getIcon(), " ", language.getTitle()));
        languageSelect.setWidthFull();
        languageSelect.setValue(Language.DEFAULT);
        return languageSelect;
    }
    
}
