package com.mar.ds.views.document.type;

import com.mar.ds.db.entity.DocumentType;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views._build.popup.UpdateViewDialog;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

public class DocumentTypeUpdateDialog extends UpdateViewDialog<DocumentType, DocumentTypeViewDialog> {

    @Override
    protected DocumentType updateEntity(DocumentType entity, BigDecimalField enumId, TextField title) {
        entity.setTitle(ViewUtils.getTextFieldValue(title));
        return entity;
    }
}
