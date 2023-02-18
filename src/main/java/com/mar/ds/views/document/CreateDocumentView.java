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

import static com.mar.ds.utils.ViewUtils.checkString;
import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static java.lang.String.format;

public class CreateDocumentView {

    public CreateDocumentView(MainView mainView) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        // title
        TextField docTitle = new TextField("Заголовок");
        docTitle.setRequired(true);
        docTitle.setWidthFull();
        // text
        TextArea textArea = new TextArea("Текст");
        textArea.setRequired(true);
        textArea.setWidthFull();
        // btn
        TextField btnTitle = new TextField("Заголовок кнопки");
        btnTitle.setRequired(true);
        btnTitle.setWidthFull();
        // img
        TextField imagePath = new TextField("Путь до изображения");
        imagePath.setWidthFull();
        // status
        List<DocumentStatus> documentStatusList = mainView.getRepositoryService().getDocumentStatusRepository().findAll();
        Select<DocumentStatus> documentStatusSelect = new Select<>();
        documentStatusSelect.setLabel("Статус");
        documentStatusSelect.setEmptySelectionAllowed(false);
        documentStatusSelect.setTextRenderer(documentStatus -> format("[%d] %32s", documentStatus.getEnumId(), documentStatus.getTitle()));
        documentStatusSelect.setDataProvider(new ListDataProvider<>(documentStatusList));
        documentStatusSelect.setWidthFull();
        // type
        List<DocumentType> documentTypeList = mainView.getRepositoryService().getDocumentTypeRepository().findAll();
        Select<DocumentType> documentTypeSelect = new Select<>();
        documentTypeSelect.setLabel("Тип");
        documentTypeSelect.setEmptySelectionAllowed(false);
        documentTypeSelect.setTextRenderer(documentStatus -> mainView.getLocalRepo().saveFindRuLocalByKey(documentStatus.getTitle()));
        documentTypeSelect.setDataProvider(new ListDataProvider<>(documentTypeList));
        documentTypeSelect.setWidthFull();

        Button crtBtn = new Button("Создать", new Icon(VaadinIcon.PLUS));
        crtBtn.addClickListener(click -> {
            try {
                if (checkString(textArea, 80)) {
                    throw new Exception("Некорректно заполнены поля");
                }
                mainView.getRepositoryService().getDocumentRepository()
                        .save(
                                Document.builder()
                                        .title(getTextFieldValue(docTitle))
                                        .text(getTextFieldValue(textArea))
                                        .btnTitle(getTextFieldValue(btnTitle))
                                        .image(getTextFieldValue(imagePath))
                                        .documentStatus(documentStatusSelect.getValue())
                                        .documentType(documentTypeSelect.getValue())
                                        .build()
                        );
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
                crtBtn.setEnabled(true);
                return;
            }
            mainView.setContent(mainView.getDocumentView().getContent());
            createDialog.close();
        });
        crtBtn.setWidthFull();
        crtBtn.setDisableOnClick(true);
        crtBtn.addClickShortcut(Key.ENTER);

        createDialog.add(
                new Label("Создать новый документ"),
                docTitle,
                textArea,
                btnTitle,
                imagePath,
                documentStatusSelect,
                documentTypeSelect,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(createDialog))
        );
        createDialog.open();
    }

}
