package com.mar.ds.views.receipt;

import com.mar.ds.db.entity.Receipt;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.views.ContentView;
import com.mar.ds.views.MainView;
import com.mar.ds.views.product.ProductViewDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.RequiredArgsConstructor;

import java.time.ZoneId;
import java.util.List;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static com.vaadin.flow.component.icon.VaadinIcon.*;

@RequiredArgsConstructor
public class ReceiptView implements ContentView {

    private final MainView appLayout;

    public VerticalLayout getContent() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        H2 label = new H2("Чё, сколько и зачем потратил");

        List<Receipt> receipts = appLayout.getRepositoryService().getReceiptRepository().findAll();

        Grid<Receipt> grid = new Grid<>();
        grid.addColumn(Receipt::getText).setHeader("Наименование");
        grid.addColumn(receipt -> receipt.getProduct().getName()).setHeader("Тип товара");
        grid.addColumn(Receipt::getPrice).setSortable(true).setHeader("Цена");
        grid.setItems(receipts);
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.addComponentColumn(receipt -> {
                    DatePicker date = new DatePicker(
                            receipt.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    date.setEnabled(false);
                    return date;
                }
        ).setHeader("Дата покупки");
        grid.addComponentColumn(receipt -> {
            Button edtBtn = new Button(new Icon(VaadinIcon.PENCIL), clk -> {
                new UpdateReceiptDialog(appLayout, receipt);
            });
            edtBtn.addThemeVariants(LUMO_TERTIARY);
            Button dltBtn = new Button(new Icon(BAN), clk -> {
                new DeleteDialogWidget(() -> {
                    appLayout.getRepositoryService().getReceiptRepository().delete(receipt);
                    appLayout.setContent(appLayout.getReceiptView().getContent());
                });
            });
            dltBtn.addThemeVariants(LUMO_TERTIARY);
            dltBtn.getStyle().set("color", "red");
            return new HorizontalLayout(edtBtn, dltBtn);
        });

        Button crtBtn = new Button("Создать запись", new Icon(PLUS), click -> new CreateReceiptDialog(appLayout));
        crtBtn.setWidthFull();

        HorizontalLayout btns = new HorizontalLayout(crtBtn, new Button(new Icon(COG), click -> new ProductViewDialog(appLayout)));

        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, label);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, grid);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, btns);

        verticalLayout.add(label, grid, btns);
        return verticalLayout;
    }
}
