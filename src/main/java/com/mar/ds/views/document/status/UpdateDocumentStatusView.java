package com.mar.ds.views.document.status;

import com.mar.ds.db.entity.DocumentStatus;
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

public class UpdateDocumentStatusView {

    public UpdateDocumentStatusView(DocumentStatusViewDialog documentStatusView, DocumentStatus updatedStatus) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);

        BigDecimalField numberField = new BigDecimalField();
        numberField.setAutofocus(true);
        numberField.setLabel("Enum number");
        numberField.setWidthFull();
        ViewUtils.setBigDecimalFieldValue(numberField, updatedStatus.getEnumId());

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setLabel("Наименование статуса");
        ViewUtils.setTextFieldValue(textField, updatedStatus.getTitle());

        Button updBtn = new Button("Обновить", new Icon(ROTATE_RIGHT));
        updBtn.addClickListener(btnEvent -> {
            try {
                updatedStatus.setTitle(ViewUtils.getTextFieldValue(textField));
                updatedStatus.setEnumId(ViewUtils.getLongValue(numberField));
                documentStatusView.getRepository().save(updatedStatus);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                updBtn.setEnabled(true);
                return;
            }
            updateDialog.close();
            documentStatusView.reloadData();
        });
        updBtn.setWidthFull();
        updBtn.setDisableOnClick(true);
        updBtn.addClickShortcut(Key.ENTER);

        updateDialog.add(
                new Label("Обновить статус документа"),
                numberField,
                textField,
                new HorizontalLayout(updBtn, ViewUtils.getCloseButton(updateDialog))
        );

        updateDialog.open();
    }

}
