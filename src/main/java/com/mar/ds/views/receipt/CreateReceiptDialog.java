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

public class CreateReceiptDialog {

    public CreateReceiptDialog(MainView mainView) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        TextField textField = new TextField("Наименование");
        textField.setWidthFull();

        List<Product> products = mainView.getRepositoryService().getProductRepository().findAll();

        Select<Product> productSelect = new Select<Product>();
        productSelect.setLabel("Продукт");
        productSelect.setPlaceholder("Выберите продукт...");
        productSelect.setTextRenderer(product -> product.getName());
        productSelect.setDataProvider(new ListDataProvider<>(products));
        productSelect.setWidthFull();

        BigDecimalField numberField = new BigDecimalField();
        numberField.setLabel("Цена");
        numberField.setWidthFull();

        DatePicker datePicker = new DatePicker("Дата покупки", LocalDate.now());
        datePicker.setWidthFull();

        Button crtBtn = new Button("Создать", new Icon(VaadinIcon.PLUS));
        crtBtn.addClickListener(click -> {
            try {
                Receipt receipt = Receipt.builder()
                        .text(textField.getValue())
                        .product(getProduct(productSelect))
                        .date(Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                        .price(getPrice(numberField))
                        .build();
                mainView.getRepositoryService().getReceiptRepository().save(receipt);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
                crtBtn.setEnabled(true);
                return;
            }
            mainView.setContent(mainView.getReceiptView().getContent());
            createDialog.close();
        });
        crtBtn.setWidthFull();
        crtBtn.setDisableOnClick(true);
        crtBtn.addClickShortcut(Key.ENTER);

        createDialog.add(
                new Label("Создать новую запись"),
                textField,
                productSelect,
                numberField,
                datePicker,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(createDialog))
        );
        createDialog.open();
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
