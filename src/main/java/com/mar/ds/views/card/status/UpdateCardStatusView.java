package com.mar.ds.views.card.status;

import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.utils.ViewUtils;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import static com.vaadin.flow.component.icon.VaadinIcon.ROTATE_RIGHT;

public class UpdateCardStatusView {

    public UpdateCardStatusView(CardStatusViewDialog cardStatusView, CardStatus updatedStatus) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setLabel("Title");
        ViewUtils.setTextFieldValue(textField, updatedStatus.getTitle());

        TextField colorField = new TextField();
        colorField.setPattern("^#(?:[0-9a-fA-F]{3}){1,2}$");
        colorField.setWidthFull();
        colorField.setLabel("Color");
        ViewUtils.setTextFieldValue(colorField, updatedStatus.getColor());

        Checkbox isRate = new Checkbox("Is rate", updatedStatus.getIsRate());

        Button updBtn = new Button("Update", new Icon(ROTATE_RIGHT));
        updBtn.addClickListener(btnEvent -> {
            try {
                updatedStatus.setTitle(ViewUtils.getTextFieldValue(textField));
                updatedStatus.setColor(ViewUtils.getTextFieldValue(colorField));
                updatedStatus.setIsRate(isRate.getValue());
                cardStatusView.getRepository().save(updatedStatus);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("ERROR", ex);
                updBtn.setEnabled(true);
                return;
            }
            updateDialog.close();
            cardStatusView.reloadData();
        });
        updBtn.setWidthFull();
        updBtn.setDisableOnClick(true);
        updBtn.addClickShortcut(Key.ENTER);

        updateDialog.add(
                new Label("Update card status"),
                textField,
                colorField,
                isRate,
                new HorizontalLayout(updBtn, ViewUtils.getCloseButton(updateDialog))
        );

        updateDialog.open();
    }

}
