package com.mar.ds.views.document.type;

import com.mar.ds.db.entity.DocumentType;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views._build.popup.CreateViewDialog;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

public class DocumentTypeCreateDialog extends CreateViewDialog<DocumentType, DocumentTypeViewDialog> {
    @Override
    protected DocumentType getNewEntity(BigDecimalField enumId, TextField title) {
        return DocumentType.builder()
                .title(ViewUtils.getTextFieldValue(title))
                .build();
    }
}
