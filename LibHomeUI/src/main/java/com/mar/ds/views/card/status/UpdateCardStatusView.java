package com.mar.ds.views.card.status;

import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.utils.ViewUtils;
import com.mar.libhome.dto.CardStatusDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

import java.awt.Color;

import static com.vaadin.flow.component.icon.VaadinIcon.ROTATE_RIGHT;

public class UpdateCardStatusView {

    public UpdateCardStatusView(CardStatusViewDialog cardStatusView, CardStatusDto updatedStatus) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setLabel("Title");
        ViewUtils.setTextFieldValue(textField, updatedStatus.getTitle());

        TextField colorField = new TextField();
        colorField.setPattern("^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$");
        colorField.setHelperText("Use HEX color value");
        colorField.setWidthFull();
        colorField.setLabel("Color");
        ViewUtils.setTextFieldValue(colorField, updatedStatus.getColor());

        TextField iconField = new TextField();
        iconField.setHelperText("Use Vaadin icon name");
        iconField.setWidthFull();
        iconField.setLabel("Icon");
        ViewUtils.setTextFieldValue(iconField, updatedStatus.getIcon());

        BigDecimalField orderField = new BigDecimalField("Order");
        orderField.setWidthFull();
        ViewUtils.setBigDecimalFieldValue(orderField, updatedStatus.getOrder());

        boolean oldRate = updatedStatus.getIsRate();
        boolean oldHasUpd = updatedStatus.getHasUpdStatus();
        Checkbox isRate = new Checkbox("Is rate", updatedStatus.getIsRate());
        Checkbox hasUpd = new Checkbox("Has UPD", updatedStatus.getHasUpdStatus());

        Button updBtn = new Button("Update", new Icon(ROTATE_RIGHT));
        updBtn.addClickListener(btnEvent -> {
            try {
                Color.decode(ViewUtils.getTextFieldValue(colorField));

                updatedStatus.setTitle(ViewUtils.getTextFieldValue(textField));
                updatedStatus.setColor(ViewUtils.getTextFieldValue(colorField));
                updatedStatus.setIcon(ViewUtils.getTextFieldValue(iconField));
                updatedStatus.setIsRate(isRate.getValue());
                updatedStatus.setHasUpdStatus(hasUpd.getValue());
                updatedStatus.setOrder(
                        ViewUtils
                                .getLongValue(orderField)
                                .orElseThrow(() -> new RuntimeException("Not set card state order."))
                );
                cardStatusView.getService().update(
                        updatedStatus,
                        CardStatusDto.builder().hasUpdStatus(oldHasUpd).isRate(oldRate).build()
                );
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

        updateDialog.add(
                new Label("Update card status"),
                textField,
                colorField,
                iconField,
                orderField,
                new HorizontalLayout(isRate, hasUpd),
                new HorizontalLayout(updBtn, ViewUtils.getCloseButton(updateDialog))
        );

        updateDialog.open();
    }

}
