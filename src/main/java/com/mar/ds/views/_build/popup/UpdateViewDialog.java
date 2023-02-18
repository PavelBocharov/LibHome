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

import static com.vaadin.flow.component.icon.VaadinIcon.ROTATE_RIGHT;


public abstract class UpdateViewDialog<E extends PopupEntity, VD extends ViewDialog> {

    private boolean withEnum = true;
    private String nameEntity = null;

    public UpdateViewDialog withoutEnumNumber() {
        withEnum = false;
        return this;
    }

    public UpdateViewDialog withNameEntity(String nameEntity) {
        this.nameEntity = nameEntity;
        return this;
    }

    public void show(VD viewDialog, E entity) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);

        BigDecimalField enumIdField = new BigDecimalField();
        enumIdField.setLabel("Enum number");
        enumIdField.setWidthFull();
        ViewUtils.setBigDecimalFieldValue(enumIdField, entity.getEntityId());

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setLabel("Наименование");
        ViewUtils.setTextFieldValue(textField, entity.getTitle());

        Button updBtn = new Button("Обновить", new Icon(ROTATE_RIGHT));
        updBtn.addClickListener(btnEvent -> {
            try {
                viewDialog.getRepository().save(updateEntity(
                        entity, enumIdField, textField
                ));
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                updBtn.setEnabled(true);
                return;
            }
            updateDialog.close();
            viewDialog.reloadData();
        });
        updBtn.setWidthFull();
        updBtn.setDisableOnClick(true);
        updBtn.addClickShortcut(Key.ENTER);

        if (withEnum) {
            updateDialog.add(
                    new Label(getLabel()),
                    enumIdField,
                    textField,
                    new HorizontalLayout(updBtn, ViewUtils.getCloseButton(updateDialog))
            );
        } else {

            updateDialog.add(
                    new Label(getLabel()),
                    textField,
                    new HorizontalLayout(updBtn, ViewUtils.getCloseButton(updateDialog))
            );
        }

        updateDialog.open();
    }

    private String getLabel() {
        return nameEntity == null ? "Обновить" : "Обновить '" + nameEntity + "'";
    }


    protected abstract E updateEntity(E entity, BigDecimalField enumId, TextField title);

}
