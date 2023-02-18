package com.mar.ds.views.document;

import com.mar.ds.db.entity.Document;
import com.mar.ds.db.entity.DocumentStatus;
import com.mar.ds.db.entity.DocumentType;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.List;

import static com.mar.ds.utils.ViewUtils.*;
import static java.lang.String.format;

public class UpdateDocumentView {

    public UpdateDocumentView(MainView mainView, Document updDoc) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);

        // title
        TextField docTitle = new TextField("Заголовок");
        docTitle.setRequired(true);
        docTitle.setWidthFull();
        setTextFieldValue(docTitle, updDoc.getTitle());
        // text
        TextArea textArea = new TextArea("Текст");
        textArea.setRequired(true);
        textArea.setWidthFull();
        setTextFieldValue(textArea, updDoc.getText());
        // btn
        TextField btnTitle = new TextField("Заголовок кнопки");
        btnTitle.setRequired(true);
        btnTitle.setWidthFull();
        setTextFieldValue(btnTitle, updDoc.getBtnTitle());
        // img
        TextField imagePath = new TextField("Путь до изображения");
        imagePath.setWidthFull();
        setTextFieldValue(imagePath, updDoc.getImage());
        // status
        List<DocumentStatus> documentStatusList = mainView.getRepositoryService().getDocumentStatusRepository().findAll();
        Select<DocumentStatus> documentStatusSelect = new Select<>();
        documentStatusSelect.setLabel("Статус");
        documentStatusSelect.setEmptySelectionAllowed(false);
        documentStatusSelect.setTextRenderer(documentStatus -> format("[%d] %32s", documentStatus.getEnumId(), documentStatus.getTitle()));
        documentStatusSelect.setDataProvider(new ListDataProvider<>(documentStatusList));
        documentStatusSelect.setWidthFull();
        ViewUtils.setSelectValue(documentStatusSelect, updDoc.getDocumentStatus(), documentStatusList);
        // type
        List<DocumentType> documentTypeList = mainView.getRepositoryService().getDocumentTypeRepository().findAll();
        Select<DocumentType> documentTypeSelect = new Select<>();
        documentTypeSelect.setLabel("Тип");
        documentTypeSelect.setEmptySelectionAllowed(false);
        documentTypeSelect.setTextRenderer(documentStatus -> mainView.getLocalRepo().saveFindRuLocalByKey(documentStatus.getTitle()));
        documentTypeSelect.setDataProvider(new ListDataProvider<>(documentTypeList));
        documentTypeSelect.setWidthFull();
        ViewUtils.setSelectValue(documentTypeSelect, updDoc.getDocumentType(), documentTypeList);

        Button updBtn = new Button("Обновить", new Icon(VaadinIcon.ROTATE_RIGHT));
        updBtn.addClickListener(click -> {
            try {
                if (checkString(textArea, 80)) {
                    throw new Exception("Некорректно заполнены поля");
                }

                updDoc.setTitle(getTextFieldValue(docTitle));
                updDoc.setText(getTextFieldValue(textArea));
                updDoc.setBtnTitle(getTextFieldValue(btnTitle));
                updDoc.setImage(getTextFieldValue(imagePath));
                updDoc.setDocumentStatus(documentStatusSelect.getValue());
                updDoc.setDocumentType(documentTypeSelect.getValue());

                mainView.getRepositoryService().getDocumentRepository()
                        .save(updDoc);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                updBtn.setEnabled(true);
                return;
            }
            mainView.setContent(mainView.getDocumentView().getContent());
            updateDialog.close();
        });
        updBtn.setWidthFull();
        updBtn.setDisableOnClick(true);
        updBtn.addClickShortcut(Key.ENTER);

        updateDialog.add(
                new Label("Обновить документ"),
                docTitle,
                textArea,
                btnTitle,
                imagePath,
                documentStatusSelect,
                documentTypeSelect,
                new HorizontalLayout(updBtn, ViewUtils.getCloseButton(updateDialog))
        );
        updateDialog.open();
    }

}
