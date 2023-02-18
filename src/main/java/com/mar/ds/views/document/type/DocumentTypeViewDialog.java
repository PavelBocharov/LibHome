package com.mar.ds.views.document.type;

import com.mar.ds.db.entity.DocumentType;
import com.mar.ds.db.jpa.DocumentTypeRepository;
import com.mar.ds.views.MainView;
import com.mar.ds.views._build.popup.ViewDialog;

public class DocumentTypeViewDialog extends ViewDialog<DocumentType, DocumentTypeRepository, DocumentTypeCreateDialog, DocumentTypeUpdateDialog> {

    public DocumentTypeViewDialog(MainView appLayout) {
        super(appLayout, "Тип документа");
    }

    @Override
    protected String getText(DocumentType entity) {
        return appLayout.getRepositoryService().getLocalizationRepository().saveFindRuLocalByKey(entity.getTitle());
    }

    @Override
    protected DocumentTypeCreateDialog getCreateViewDialog() {
        return (DocumentTypeCreateDialog) new DocumentTypeCreateDialog()
                .withoutEnumNumber()
                .withNameEntity("Тип документа");
    }

    @Override
    protected DocumentTypeUpdateDialog getUpdateViewDialog() {
        return (DocumentTypeUpdateDialog) new DocumentTypeUpdateDialog()
                .withoutEnumNumber()
                .withNameEntity("Тип документа")
                ;
    }

    public DocumentTypeRepository getRepository() {
        return appLayout.getRepositoryService().getDocumentTypeRepository();
    }
}
