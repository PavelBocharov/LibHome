package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.CardType;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.db.entity.GameEngine;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.mar.ds.utils.ViewUtils.getDoubleValue;
import static com.mar.ds.utils.ViewUtils.getTextFieldValue;

@Slf4j
public class CreateCardView {

    final int minPoint;
    final int maxPoint;
    private BigDecimalField point;

    public CreateCardView(MainView mainView) {
        minPoint = Integer.parseInt(mainView.getEnv().getProperty("card.point.min", "0"));
        maxPoint = Integer.parseInt(mainView.getEnv().getProperty("card.point.max", "10"));

        Dialog createDialog = new Dialog();
        createDialog.setMaxHeight(80, Unit.PERCENTAGE);
        createDialog.setMaxWidth(80, Unit.PERCENTAGE);
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
        point = new BigDecimalField("Point");
        point.setValue(BigDecimal.valueOf(minPoint));
        point.setHelperText(String.format("Min value = %d, max value = %d", minPoint, maxPoint));
        point.setWidthFull();
        // engine
        Select<GameEngine> engineSelect = new Select<>(GameEngine.values());
        engineSelect.setLabel("Engine");
        engineSelect.setEmptySelectionAllowed(false);
        engineSelect.setTextRenderer(GameEngine::getName);
        engineSelect.setWidthFull();
        engineSelect.setValue(GameEngine.RENPY);
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
        cardStatusListSelect.setRequiredIndicatorVisible(true);
        // type
        List<CardType> cardTypeList = mainView.getRepositoryService().getCardTypeRepository().findAll();
        Select<CardType> cardTypeListSelect = new Select<>();
        cardTypeListSelect.setLabel("Type");
        cardTypeListSelect.setEmptySelectionAllowed(false);
        cardTypeListSelect.setTextRenderer(CardType::getTitle);
        cardTypeListSelect.setDataProvider(new ListDataProvider<>(cardTypeList));
        cardTypeListSelect.setWidthFull();
        cardTypeListSelect.setRequiredIndicatorVisible(true);
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
            log.info("Load tagList: {}", tagList);
            tags.setItems(tagList);
        });
        // link
        TextField link = new TextField("Link");
        link.setRequired(true);
        link.setWidthFull();

        Button crtBtn = new Button("Create", new Icon(VaadinIcon.PLUS));
        crtBtn.addClickListener(click -> {
            try {
                checkValues();
                mainView.getRepositoryService().getCardRepository()
                        .save(
                                Card.builder()
                                        .title(getTextFieldValue(cardTitle))
                                        .info(getTextFieldValue(infoArea))
                                        .link(getTextFieldValue(link))
                                        .engine(engineSelect.getValue())
                                        .point(getDoubleValue(point))
                                        .cardStatus(cardStatusListSelect.getValue())
                                        .cardType(cardTypeListSelect.getValue())
                                        .lastUpdate(Date.from(lastUpdDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                                        .lastGame(Date.from(lastGameDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                                        .tagList(tags.getValue().stream().toList())
                                        .build()
                        );
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
                new HorizontalLayout(point, engineSelect),
                link,
                new HorizontalLayout(lastUpdDate, lastGameDate),
                new HorizontalLayout(cardStatusListSelect, cardTypeListSelect, tags),
                infoArea,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(createDialog))
        );
        createDialog.open();
    }

    private void checkValues() throws Exception {
        double cardPoint = getDoubleValue(point);
        if (minPoint > cardPoint || maxPoint < cardPoint) {
            point.setInvalid(true);
            throw new Exception(String.format("Point value ERROR.\nMin value = %d, max value = %d", minPoint, maxPoint));
        } else {
            point.setInvalid(false);
        }
    }

}
