package com.mar.ds.views.dialog.teleport.generate;

import com.mar.ds.db.entity.GenerateType;
import com.mar.ds.utils.ViewUtils;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

import java.math.BigDecimal;

import static com.vaadin.flow.component.icon.VaadinIcon.ROTATE_RIGHT;

public class UpdateGenerateTypeView {


    public UpdateGenerateTypeView(GenerateTypeDialogView generateTypeDialogView, GenerateType generateType) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);

        TextField idField = new TextField();
        idField.setWidthFull();
        idField.setAutofocus(false);
        idField.setEnabled(false);
        idField.setLabel("ID");
        idField.setValue(String.valueOf(generateType.getId()));

        BigDecimalField numberField = new BigDecimalField();
        numberField.setAutofocus(true);
        numberField.setLabel("Enum number");
        numberField.setWidthFull();
        numberField.setValue(BigDecimal.valueOf(generateType.getEnumNumber()));

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setLabel("Наименование типа");
        textField.setValue(generateType.getName());

        Button uptBtn = new Button("Обновить", new Icon(ROTATE_RIGHT));
        uptBtn.addClickListener(btnEvent -> {
            try {
                generateType.setName(textField.getValue());
                generateType.setEnumNumber(numberField.getValue().longValue());
                generateTypeDialogView.getRepository()
                        .save(generateType);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                uptBtn.setEnabled(true);
                return;
            }
            updateDialog.close();
            generateTypeDialogView.reloadData();
        });
        uptBtn.setWidthFull();
        uptBtn.setDisableOnClick(true);
        uptBtn.addClickShortcut(Key.ENTER);

        updateDialog.add(
                new Label("Обновить статус предмета"),
                idField,
                numberField,
                textField,
                new HorizontalLayout(uptBtn, ViewUtils.getCloseButton(updateDialog))
        );

        updateDialog.open();
    }


}
