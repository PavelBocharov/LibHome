package com.mar.ds.views.card.status;

import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.utils.ViewUtils;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;

public class CreateCardStatusView {

    public CreateCardStatusView(CardStatusViewDialog cardStatusView) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setLabel("Наименование статуса");

        TextField colorField = new TextField();
        colorField.setPattern("^#(?:[0-9a-fA-F]{3}){1,2}$");
        colorField.setWidthFull();
        colorField.setLabel("Color");

        Button createBtn = new Button("Создать", new Icon(PLUS));
        createBtn.addClickListener(btnEvent -> {
            try {
                cardStatusView.getRepository().save(
                        CardStatus.builder()
                                .title(ViewUtils.getTextFieldValue(textField))
                                .color(ViewUtils.getTextFieldValue(colorField))
                                .build()
                );
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
                createBtn.setEnabled(true);
                return;
            }
            createDialog.close();
            cardStatusView.reloadData();
        });
        createBtn.setWidthFull();
        createBtn.setDisableOnClick(true);
        createBtn.addClickShortcut(Key.ENTER);

        createDialog.add(
                new Label("Создать новый статус"),
                textField,
                colorField,
                new HorizontalLayout(createBtn, ViewUtils.getCloseButton(createDialog))
        );

        createDialog.open();
    }

}
