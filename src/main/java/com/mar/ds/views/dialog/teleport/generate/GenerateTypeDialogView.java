package com.mar.ds.views.dialog.teleport.generate;

import com.mar.ds.db.entity.GenerateType;
import com.mar.ds.db.jpa.GenerateTypeRepository;
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

public class GenerateTypeDialogView {

    private final MainView appLayout;
    private final Dialog dialog;
    private final Button crtBtn;
    private VerticalLayout generateTypes;

    public GenerateTypeDialogView(MainView appLayout) {
        this.appLayout = appLayout;

        dialog = new Dialog();

        crtBtn = new Button("Создать тип генерируемой локации", new Icon(VaadinIcon.PLUS));
        crtBtn.setWidthFull();
        crtBtn.addClickListener(btnClick -> new CreateGenerateTypeView(this));

        reloadData();

        dialog.open();
    }


    private void initProducts() {
        generateTypes = new VerticalLayout();

        List<GenerateType> generateTypeList = getRepository().findAll();

        for (GenerateType generateType : generateTypeList) {
            TextField name = new TextField();
            name.setTitle("Name");
            name.setEnabled(false);
            name.setWidthFull();
            name.setValue(format("[%d] %s", generateType.getEnumNumber(), generateType.getName()));

            Button dltBtn = new Button(new Icon(VaadinIcon.BAN), buttonClickEvent -> {
                try {
                    new DeleteDialogWidget(() -> {
                        getRepository().delete(generateType);
                        reloadData();
                    });
                } catch (Exception ex) {
                    ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
                }
            });
            dltBtn.getStyle().set("color", "red");

            Button uptBtn = new Button(
                    new Icon(VaadinIcon.PENCIL),
                    buttonClickEvent -> new UpdateGenerateTypeView(this, generateType)
            );
            generateTypes.add(new HorizontalLayout(name, uptBtn, dltBtn));
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
        appLayout.setContent(appLayout.getActionView().getContent());
        dialog.removeAll();
        dialog.add(
                new Label("Список типов генерируемых локаций"),
                generateTypes,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(dialog))
        );
    }

    public GenerateTypeRepository getRepository() {
        return appLayout.getRepositoryService().getGenerateTypeRepository();
    }

}
