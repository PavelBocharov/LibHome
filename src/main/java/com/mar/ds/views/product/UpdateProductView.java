package com.mar.ds.views.product;

import com.mar.ds.db.entity.Product;
import com.mar.ds.utils.ViewUtils;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import static com.vaadin.flow.component.icon.VaadinIcon.PENCIL;

public class UpdateProductView {

    public UpdateProductView(ProductViewDialog productView, Product product) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);

        TextField idField = new TextField();
        idField.setWidthFull();
        idField.setAutofocus(false);
        idField.setEnabled(false);
        idField.setLabel("ID группы товара");
        idField.setValue(String.valueOf(product.getId()));

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setAutofocus(true);
        textField.setLabel("Наименование группы товара");
        textField.setValue(product.getName());

        Button uptBtn = new Button("Обновить", new Icon(PENCIL));
        uptBtn.addClickListener(btnEvent -> {
            Long id = Long.valueOf(idField.getValue());
            String name = textField.getValue();
            try {
                productView.getRepository().save(Product.builder().id(id).name(name).build());
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                uptBtn.setEnabled(true);
                return;
            }
            updateDialog.close();
            productView.reloadData();
        });
        uptBtn.setWidthFull();
        uptBtn.setDisableOnClick(true);
        uptBtn.addClickShortcut(Key.ENTER);

        updateDialog.add(
                new Label("Обновить группу"),
                idField,
                textField,
                new HorizontalLayout(uptBtn, ViewUtils.getCloseButton(updateDialog))
        );

        updateDialog.open();
    }

}
