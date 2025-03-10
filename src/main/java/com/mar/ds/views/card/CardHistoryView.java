package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardHistory;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import static com.mar.ds.db.diff.DiffCard.CARD_LAST_GAME_DATE;
import static com.mar.ds.db.diff.DiffCard.CARD_LAST_UPD_DATE;
import static com.mar.ds.utils.Utils.formatUsingSimpleDateTimeFormat;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

/**
 * Диалоговое окно с таблицей изменения карточки.
 */
@Slf4j
public class CardHistoryView extends Dialog {

    private final MainView mainView;
    private final Card card;

    /**
     * Конструктор.
     *
     * @param mainView родительское окно.
     * @param card     по какой карточке будет история.
     */
    public CardHistoryView(MainView mainView, Card card) {
        assert nonNull(mainView);
        assert nonNull(card);

        this.mainView = mainView;
        this.card = card;

        this.setWidth(70, Unit.PERCENTAGE);
        this.setHeight(90, Unit.PERCENTAGE);
    }

    private String saveGetDate(String maybeDate) {
        try {
            return formatUsingSimpleDateTimeFormat(new Date(Long.parseLong(maybeDate)));
        } catch (Exception e) {
            return maybeDate;
        }
    }

    private void init() {
        this.removeAll();
        Grid<CardHistory> historyGrid = new Grid<>();
        historyGrid.setSizeFull();

        historyGrid.addColumn(CardHistory::getColumnName)
                .setHeader("Column name");
        historyGrid.addColumn(CardHistory::getUpdateCardTime)
                .setHeader("UPD date")
                .setSortable(true);
        historyGrid.addColumn(cardHistory ->
                switch (cardHistory.getColumnName()) {
                    case CARD_LAST_GAME_DATE, CARD_LAST_UPD_DATE: {
                        yield saveGetDate(cardHistory.getOldValue());
                    }
                    default:
                        yield cardHistory.getOldValue();
                }
        ).setHeader("Old value");
        historyGrid.addColumn(cardHistory ->
                switch (cardHistory.getColumnName()) {
                    case CARD_LAST_GAME_DATE, CARD_LAST_UPD_DATE: {
                        yield saveGetDate(cardHistory.getNewValue());
                    }
                    default:
                        yield cardHistory.getNewValue();
                }
        ).setHeader("New value");

        historyGrid.setItems(
                mainView.getCardHistoryService().findAllByCardId(card.getId())
        );

        Button backBtn = new Button("Back", VaadinIcon.ARROW_BACKWARD.create());
        backBtn.addClickListener(buttonClickEvent -> this.close());
        backBtn.setWidthFull();

        VerticalLayout verticalLayout = new VerticalLayout(
                new H3(format("Card history: [%d] %s", card.getId(), card.getTitle())),
                historyGrid,
                backBtn
        );
        verticalLayout.setSizeFull();
        verticalLayout.getStyle().set("padding", "0px");
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, historyGrid);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, backBtn);
        this.add(verticalLayout);
    }

    @Override
    public void open() {
        init();
        super.open();
    }
}
