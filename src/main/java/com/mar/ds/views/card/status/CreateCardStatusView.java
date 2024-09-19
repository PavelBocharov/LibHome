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
        colorField.setHelperText("Use HEX or string text (red, green ant etc.)");
        colorField.setWidthFull();
        colorField.setLabel("Color");

        TextField iconField = new TextField();
        iconField.setHelperText("Use Vaadin icon name");
        iconField.setWidthFull();
        iconField.setLabel("Icon");

        Checkbox isRate = new Checkbox("Is rate", false);

        Button createBtn = new Button("Create", new Icon(PLUS));
        createBtn.addClickListener(btnEvent -> {
            try {
                cardStatusView.getRepository().save(
                        CardStatus.builder()
                                .title(ViewUtils.getTextFieldValue(textField))
                                .color(ViewUtils.getTextFieldValue(colorField))
                                .icon(ViewUtils.getTextFieldValue(iconField))
                                .isRate(isRate.getValue())
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
        createBtn.addClickShortcut(Key.ENTER);

        createDialog.add(
                new Label("Create card status"),
                textField,
                colorField,
                iconField,
                isRate,
                new HorizontalLayout(createBtn, ViewUtils.getCloseButton(createDialog))
        );

        createDialog.open();
    }

}
