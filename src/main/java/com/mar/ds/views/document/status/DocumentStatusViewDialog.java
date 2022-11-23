package com.mar.ds.views.document.status;

import com.mar.ds.db.entity.DocumentStatus;
import com.mar.ds.db.jpa.DocumentStatusRepository;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;

import static java.lang.String.format;

public class DocumentStatusViewDialog {
    private final MainView appLayout;
    private Dialog dialog;
    private VerticalLayout docStatusList;
    private Button crtBtn;

    public DocumentStatusViewDialog(MainView appLayout) {
        this.appLayout = appLayout;

        dialog = new Dialog();

        crtBtn = new Button("Создать статус документа", new Icon(VaadinIcon.PLUS));
        crtBtn.setWidthFull();
        crtBtn.addClickListener(btnClick -> new CreateDocumentStatusView(this));

        reloadData();

        dialog.open();
    }


    private void initProducts() {
        docStatusList = new VerticalLayout();

        List<DocumentStatus> documentStatusList = getRepository().findAll();

        for (DocumentStatus documentStatus : documentStatusList) {
            TextField name = new TextField();
            name.setTitle("Name");
            name.setEnabled(false);
            name.setWidthFull();
            name.setValue(format("[%d] %s", documentStatus.getEnumId(), documentStatus.getTitle()));

            Button dltBtn = new Button(new Icon(VaadinIcon.BAN), buttonClickEvent -> {
                try {
                    new DeleteDialogWidget(() -> {
                        getRepository().delete(documentStatus);
                        reloadData();
                    });
                } catch (Exception ex) {
                    ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
                    return;
                }
            });
            dltBtn.getStyle().set("color", "red");

            Button uptBtn = new Button(
                    new Icon(VaadinIcon.PENCIL),
                    buttonClickEvent -> new UpdateDocumentStatusView(this, documentStatus)
            );
            this.docStatusList.add(new HorizontalLayout(name, uptBtn, dltBtn));
        }
    }

    public void reloadData() {
        try {
            initProducts();
        } catch (Exception ex) {
            ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
            crtBtn.setEnabled(true);
            return;
        }
        appLayout.setContent(appLayout.getDocumentView().getContent());
        dialog.removeAll();
        dialog.add(
                new Label("Список статусов документа"),
                docStatusList,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(dialog))
        );
    }

    public DocumentStatusRepository getRepository() {
        return appLayout.getRepositoryService().getDocumentStatusRepository();
    }
}
