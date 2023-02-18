package com.mar.ds.views._build.popup;

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

public abstract class CreateViewDialog<E extends PopupEntity, VD extends ViewDialog> {

    private boolean withEnum = true;
    private String nameEntity = null;

    public CreateViewDialog withoutEnumNumber() {
        withEnum = false;
        return this;
    }

    public CreateViewDialog withNameEntity(String nameEntity) {
        this.nameEntity = nameEntity;
        return this;
    }

    public void show(VD viewDialog) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        BigDecimalField enumIdField = new BigDecimalField();
        enumIdField.setAutofocus(true);
        enumIdField.setLabel("Enum number");
        enumIdField.setWidthFull();

        TextField titleField = new TextField();
        titleField.setWidthFull();
        titleField.setLabel("Наименование");

        Button createBtn = new Button("Создать", new Icon(PLUS));
        createBtn.addClickListener(btnEvent -> {
            try {
                viewDialog.getRepository().save(getNewEntity(enumIdField, titleField));
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
                createBtn.setEnabled(true);
                return;
            }
            createDialog.close();
            viewDialog.reloadData();
        });
        createBtn.setWidthFull();
        createBtn.setDisableOnClick(true);
        createBtn.addClickShortcut(Key.ENTER);

        if (withEnum) {
            createDialog.add(
                    new Label(getLabel()),
                    enumIdField,
                    titleField,
                    new HorizontalLayout(createBtn, ViewUtils.getCloseButton(createDialog))
            );
        } else {
            createDialog.add(
                    new Label(getLabel()),
                    titleField,
                    new HorizontalLayout(createBtn, ViewUtils.getCloseButton(createDialog))
            );
        }

        createDialog.open();
    }

    private String getLabel() {
        return nameEntity == null ? "Создать" : "Создать '" + nameEntity + "'";
    }

    protected abstract E getNewEntity(BigDecimalField enumId, TextField title);
}
