package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.Language;
import com.mar.ds.utils.FileUtils;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static com.mar.ds.data.GridInfo.GRID_DATE_GAME;
import static com.mar.ds.data.GridInfo.GRID_DATE_UPD;
import static com.mar.ds.data.GridInfo.GRID_LANGUAGE;
import static com.mar.ds.utils.ViewUtils.getValue;
import static com.mar.ds.utils.ViewUtils.setSelectValue;
import static com.mar.ds.utils.ViewUtils.setValue;

public class FastUpdateCardView extends CardDialogView {

    private final Dialog dialog;
    private final Card card;

    public FastUpdateCardView(MainView mainView, Card updCard) {
        this.mainView = mainView;
        this.card = updCard;
        this.viewType = card.getViewType();

        dialog = new Dialog();
        dialog.add(new Label("Fast update card info"));
        dialog.setMaxHeight(80, Unit.PERCENTAGE);
        dialog.setMaxWidth(80, Unit.PERCENTAGE);
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        if (getTitles().containsKey(GRID_LANGUAGE)) {
            dialog.add(getLanguageSelector());
            setSelectValue(languageSelect, card.getLanguage(), Language.values(), Language.DEFAULT);
        }
        if (getTitles().containsKey(GRID_DATE_UPD)) {
            dialog.add(getUpdDate());
            setValue(updDate, card.getLastUpdate());
        }
        if (getTitles().containsKey(GRID_DATE_GAME)) {
            dialog.add(getGameDate());
            setValue(gameDate, card.getLastGame());
        }

        Button updBtn = new Button("Update", new Icon(VaadinIcon.ROTATE_RIGHT));
        updBtn.setWidthFull();
        updBtn.setDisableOnClick(true);
        updBtn.addClickShortcut(Key.ENTER);
        updBtn.addClickListener(buttonClickEvent -> {
            if (getTitles().containsKey(GRID_LANGUAGE)) {
                updCard.setLanguage(Optional.ofNullable(languageSelect.getValue()).orElse(Language.DEFAULT));
            }
            if (getTitles().containsKey(GRID_DATE_UPD)) {
                updCard.setLastUpdate(getValue(updDate, new Date()));
                if (!getTitles().containsKey(GRID_DATE_GAME)) {
                    updCard.setLastGame(new Date());
                }
            }
            if (getTitles().containsKey(GRID_DATE_GAME)) {
                updCard.setLastGame(getValue(gameDate, new Date()));
            }
            mainView.getRepositoryService().getCardRepository().save(updCard);
            mainView.getActiveView().reloadData();
            dialog.close();
        });

        dialog.add(new HorizontalLayout(updBtn, ViewUtils.getCloseButton(dialog)));
    }


    @Override
    public void showDialog() {
        dialog.open();
    }

    @Override
    public void closeDialog() {
        dialog.close();
    }
}
