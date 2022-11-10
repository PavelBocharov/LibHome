package com.mar.ds.views.item.itemType;

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

import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;

public class CreateItemTypeView {

    public CreateItemTypeView(ItemTypeViewDialog itemTypeView) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        BigDecimalField numberField = new BigDecimalField();
        numberField.setAutofocus(true);
        numberField.setLabel("Enum number");
        numberField.setWidthFull();

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setLabel("Наименование типа");

        Button createBtn = new Button("Создать", new Icon(PLUS));
        createBtn.addClickListener(btnEvent -> {
            try {
                String name = textField.getValue();
                Long enumNumber = numberField.getValue().longValue();
                itemTypeView.getRepository().save(ItemType.builder().name(name).enumNumber(enumNumber).build());
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
                createBtn.setEnabled(true);
                return;
            }
            createDialog.close();
            itemTypeView.reloadData();
        });
        createBtn.setWidthFull();
        createBtn.setDisableOnClick(true);
        createBtn.addClickShortcut(Key.ENTER);

        createDialog.add(
                new Label("Создать новый тип"),
                numberField,
                textField,
                new HorizontalLayout(createBtn, ViewUtils.getCloseButton(createDialog))
        );

        createDialog.open();
    }

}
