package com.mar.ds.views.card.status;

import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.utils.ViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

import java.awt.Color;

import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;

public class CreateCardStatusView {

    public CreateCardStatusView(CardStatusViewDialog cardStatusView) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setLabel("Title");

        TextField colorField = new TextField();
        colorField.setPattern("^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$");
        colorField.setHelperText("Use HEX color value");
        colorField.setWidthFull();
        colorField.setLabel("Color");

        TextField iconField = new TextField();
        iconField.setHelperText("Use Vaadin icon name");
        iconField.setWidthFull();
        iconField.setLabel("Icon");

        BigDecimalField orderField = new BigDecimalField("Order");
        orderField.setWidthFull();

        Checkbox isRate = new Checkbox("Is rate", false);
        Checkbox hasUpd = new Checkbox("Has UPD", false);

        Button createBtn = new Button("Create", new Icon(PLUS));
        createBtn.addClickListener(btnEvent -> {
            try {
                Color.decode(ViewUtils.getTextFieldValue(colorField));

                cardStatusView.getService().save(
                        CardStatus.builder()
                                .title(ViewUtils.getTextFieldValue(textField))
                                .color(ViewUtils.getTextFieldValue(colorField))
                                .icon(ViewUtils.getTextFieldValue(iconField))
                                .isRate(isRate.getValue())
                                .hasUpdStatus(hasUpd.getValue())
                                .order(
                                        ViewUtils
                                                .getLongValue(orderField)
                                                .orElseThrow(() -> new RuntimeException("Not set card status order."))
                                )
                                .build()
                );
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("Error", ex);
                createBtn.setEnabled(true);
                return;
            }
            createDialog.close();
            cardStatusView.reloadData();
        });
        createBtn.setWidthFull();
        createBtn.setDisableOnClick(true);

        createDialog.add(
                new Label("Create card status"),
                textField,
                colorField,
                iconField,
                orderField,
                new HorizontalLayout(isRate, hasUpd),
                new HorizontalLayout(createBtn, ViewUtils.getCloseButton(createDialog))
        );

        createDialog.open();
    }

}
