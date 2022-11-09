package com.mar.ds.views.receipt;

import com.mar.ds.db.entity.Product;
import com.mar.ds.db.entity.Receipt;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class UpdateReceiptDialog {

    public UpdateReceiptDialog(MainView mainView, Receipt receipt) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);

        List<Product> products = mainView.getRepositoryService().getProductRepository().findAll();

        TextField idField = new TextField();
        idField.setWidthFull();
        idField.setAutofocus(false);
        idField.setEnabled(false);
        idField.setLabel("ID");
        idField.setValue(String.valueOf(receipt.getId()));

        TextField textField = new TextField("Наименование");
        textField.setValue(receipt.getText());
        textField.setWidthFull();

        Select<Product> productSelect = new Select<Product>();
        productSelect.setLabel("Продукт");
        productSelect.setPlaceholder("Выберите продукт...");
        productSelect.setTextRenderer(product -> product.getName());
        productSelect.setDataProvider(new ListDataProvider<>(products));
        productSelect.setWidthFull();
        productSelect.setValue(receipt.getProduct());

        BigDecimalField numberField = new BigDecimalField();
        numberField.setLabel("Цена");
        numberField.setWidthFull();
        numberField.setValue(BigDecimal.valueOf(receipt.getPrice()));

        DatePicker datePicker = new DatePicker("Дата покупки", LocalDate.now());
        datePicker.setWidthFull();
        datePicker.setValue(receipt.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        Button uptBtn = new Button("Обновить", new Icon(VaadinIcon.PLUS));
        uptBtn.addClickListener(click -> {
            try {
                receipt.setText(textField.getValue());
                receipt.setProduct(getProduct(productSelect));
                receipt.setDate(Date.from(
                        datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()
                ));
                receipt.setPrice(getPrice(numberField));
                mainView.getRepositoryService().getReceiptRepository().save(receipt);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                uptBtn.setEnabled(true);
                return;
            }
            mainView.setContent(mainView.getReceiptView().getContent());
            updateDialog.close();
        });
        uptBtn.setWidthFull();
        uptBtn.setDisableOnClick(true);
        uptBtn.addClickShortcut(Key.ENTER);

        updateDialog.add(
                new Label("Обновить запись"),
                idField,
                textField,
                productSelect,
                numberField,
                datePicker,
                new HorizontalLayout(uptBtn, ViewUtils.getCloseButton(updateDialog))
        );
        updateDialog.open();
    }

    private Product getProduct(Select<Product> productSelect) {
        Product product = productSelect.getValue();
        if (product == null) {
            productSelect.setErrorMessage("Выберите продукт");
            throw new IllegalArgumentException("Не выбран продукт");
        }
        return product;
    }

    private float getPrice(BigDecimalField numberField) {
        BigDecimal price = numberField.getValue();
        if (price == null) {
            numberField.setErrorMessage("Введите корректную цену");
            throw new IllegalArgumentException("Не введена корректная цена");
        }
        return price.floatValue();
    }

}
