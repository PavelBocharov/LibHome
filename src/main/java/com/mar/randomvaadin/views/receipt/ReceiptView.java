package com.mar.randomvaadin.views.receipt;

import com.google.common.collect.ImmutableList;
import com.mar.randomvaadin.db.entity.Product;
import com.mar.randomvaadin.db.entity.Receipt;
import com.mar.randomvaadin.views.MainView;
import com.mar.randomvaadin.views.product.ProductViewDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class ReceiptView {

    private final MainView appLayout;

    public VerticalLayout getContent() {
        VerticalLayout verticalLayout = new VerticalLayout();

        Random random = new Random();
        List<Receipt> receipts = ImmutableList.of(
                Receipt.builder().product(Product.builder().name("Fish").build()).date(new Date()).price(random.nextFloat()).build(),
                Receipt.builder().product(Product.builder().name("Fish").build()).date(new Date()).price(random.nextFloat()).build(),
                Receipt.builder().product(Product.builder().name("Fish").build()).date(new Date()).price(random.nextFloat()).build(),
                Receipt.builder().product(Product.builder().name("Fish").build()).date(new Date()).price(random.nextFloat()).build()
        );

        Grid<Receipt> grid = new Grid<>(3);
        grid.addColumn(receipt -> receipt.getProduct().getName()).setHeader("Product");
        grid.addColumn(Receipt::getPrice).setSortable(true).setHeader("Price");
        grid.addColumn(Receipt::getDate).setHeader("Date");

        grid.setItems(receipts);

        Button test = new Button(new Icon(VaadinIcon.MOBILE));
        test.addClickListener(click -> {
            new ProductViewDialog(appLayout);
        });

        verticalLayout.add(
                grid,
                new HorizontalLayout(
                        test
                )
        );
        return verticalLayout;
    }
}
