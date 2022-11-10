package com.mar.ds.views.itemType;

import com.mar.ds.db.entity.ItemType;
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

import static com.vaadin.flow.component.icon.VaadinIcon.PENCIL;

public class UpdateItemTypeView {

    public UpdateItemTypeView(ItemTypeViewDialog itemTypeViewDialog, ItemType itemType) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);

        TextField idField = new TextField();
        idField.setWidthFull();
        idField.setAutofocus(false);
        idField.setEnabled(false);
        idField.setLabel("ID");
        idField.setValue(String.valueOf(itemType.getId()));

        BigDecimalField numberField = new BigDecimalField();
        numberField.setAutofocus(true);
        numberField.setLabel("Enum number");
        numberField.setWidthFull();
        numberField.setValue(BigDecimal.valueOf(itemType.getEnumNumber()));

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setLabel("Наименование типа");
        textField.setValue(itemType.getName());

        Button uptBtn = new Button("Обновить", new Icon(PENCIL));
        uptBtn.addClickListener(btnEvent -> {
            try {
                Long id = Long.valueOf(idField.getValue());
                String name = textField.getValue();
                Long enumNumber = numberField.getValue().longValue();
                itemTypeViewDialog.getRepository().save(ItemType.builder().id(id).name(name).enumNumber(enumNumber).build());
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                uptBtn.setEnabled(true);
                return;
            }
            updateDialog.close();
            itemTypeViewDialog.reloadData();
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
