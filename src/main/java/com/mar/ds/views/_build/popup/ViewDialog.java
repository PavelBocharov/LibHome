package com.mar.ds.views._build.popup;

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
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class ViewDialog<E extends PopupEntity, Repo extends JpaRepository<E, Long>, CVD extends CreateViewDialog, UVD extends UpdateViewDialog> {

    protected final MainView appLayout;
    private String nameEntity;
    private Dialog dialog;
    private VerticalLayout docTypeList;
    private Button crtBtn;

    public ViewDialog(MainView appLayout) {
        this.appLayout = appLayout;
        init(null);
    }

    public ViewDialog(MainView appLayout, String nameEntity) {
        this.appLayout = appLayout;
        init(nameEntity);
    }

    private void init(String nameEntity) {
        this.nameEntity = nameEntity;

        dialog = new Dialog();

        crtBtn = new Button("Создать", new Icon(VaadinIcon.PLUS));
        crtBtn.setWidthFull();
        crtBtn.addClickListener(btnClick -> getCreateViewDialog().show(this));

        reloadData();

        dialog.open();
    }

    private void initProducts() {
        docTypeList = new VerticalLayout();
        docTypeList.setWidthFull();

        List<E> entityList = getRepository().findAll();

        for (E entity : entityList) {
            TextField name = new TextField();
            name.setTitle("Name");
            name.setEnabled(false);
            name.setWidthFull();
            name.setValue(getText(entity));

            Button dltBtn = new Button(new Icon(VaadinIcon.BAN), buttonClickEvent -> {
                try {
                    new DeleteDialogWidget(() -> {
                        getRepository().delete(entity);
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
                    buttonClickEvent -> getUpdateViewDialog().show(this, entity)
            );
            this.docTypeList.add(new HorizontalLayout(name, uptBtn, dltBtn));
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
                new Label(getLabel()),
                docTypeList,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(dialog))
        );
    }

    private String getLabel() {
        return nameEntity == null ? "Список" : "Список '" + nameEntity + "'";
    }

    protected abstract String getText(E entity);
    protected abstract CVD getCreateViewDialog();
    protected abstract UVD getUpdateViewDialog();
    protected abstract Repo getRepository();
}
