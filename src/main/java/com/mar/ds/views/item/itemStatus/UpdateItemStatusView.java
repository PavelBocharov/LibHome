package com.mar.ds.views.item.itemStatus;

import com.mar.ds.db.entity.ItemStatus;
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
import static com.vaadin.flow.component.icon.VaadinIcon.ROTATE_RIGHT;

public class UpdateItemStatusView {

    public UpdateItemStatusView(ItemStatusViewDialog itemStatusView, ItemStatus itemStatus) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);

        TextField idField = new TextField();
        idField.setWidthFull();
        idField.setAutofocus(false);
        idField.setEnabled(false);
        idField.setLabel("ID");
        idField.setValue(String.valueOf(itemStatus.getId()));

        BigDecimalField numberField = new BigDecimalField();
        numberField.setAutofocus(true);
        numberField.setLabel("Enum number");
        numberField.setWidthFull();
        numberField.setValue(BigDecimal.valueOf(itemStatus.getEnumNumber()));

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setLabel("Наименование статуса");
        textField.setValue(itemStatus.getName());

        Button uptBtn = new Button("Обновить", new Icon(ROTATE_RIGHT));
        uptBtn.addClickListener(btnEvent -> {
            try {
                Long id = Long.valueOf(idField.getValue());
                String name = textField.getValue();
                Long enumNumber = numberField.getValue().longValue();
                itemStatusView.getRepository().save(ItemStatus.builder().id(id).name(name).enumNumber(enumNumber).build());
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                uptBtn.setEnabled(true);
                return;
            }
            updateDialog.close();
            itemStatusView.reloadData();
        });
        uptBtn.setWidthFull();
        uptBtn.setDisableOnClick(true);
        uptBtn.addClickShortcut(Key.ENTER);

        updateDialog.add(
                new Label("Обновить список"),
                idField,
                numberField,
                textField,
                new HorizontalLayout(uptBtn, ViewUtils.getCloseButton(updateDialog))
        );

        updateDialog.open();
    }

}
