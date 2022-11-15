package com.mar.ds.views.item.itemStatus;

import com.mar.ds.db.entity.ItemStatus;
import com.mar.ds.db.jpa.ItemStatusRepository;
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

public class ItemStatusViewDialog {
    private final MainView appLayout;
    private Dialog dialog;
    private VerticalLayout itemStatusList;
    private Button crtBtn;

    public ItemStatusViewDialog(MainView appLayout) {
        this.appLayout = appLayout;

        dialog = new Dialog();

        crtBtn = new Button("Создать статус предмета", new Icon(VaadinIcon.PLUS));
        crtBtn.setWidthFull();
        crtBtn.addClickListener(btnClick -> new CreateItemStatusView(this));

        reloadData();

        dialog.open();
    }


    private void initProducts() {
        itemStatusList = new VerticalLayout();

        List<ItemStatus> itemStatusList = getRepository().findAll();

        for (ItemStatus itemStatus : itemStatusList) {
            TextField name = new TextField();
            name.setTitle("Name");
            name.setEnabled(false);
            name.setWidthFull();
            name.setValue(format("[%d] %s", itemStatus.getEnumNumber(), itemStatus.getName()));

            Button dltBtn = new Button(new Icon(VaadinIcon.BAN), buttonClickEvent -> {
                try {
                    new DeleteDialogWidget(() -> {
                        getRepository().delete(itemStatus);
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
                    buttonClickEvent -> new UpdateItemStatusView(this, itemStatus)
            );
            this.itemStatusList.add(new HorizontalLayout(name, uptBtn, dltBtn));
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
        appLayout.setContent(appLayout.getItemView().getContent());
        dialog.removeAll();
        dialog.add(
                new Label("Список статусов предметов"),
                itemStatusList,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(dialog))
        );
    }

    public ItemStatusRepository getRepository() {
        return appLayout.getRepositoryService().getItemStatusRepository();
    }
}
