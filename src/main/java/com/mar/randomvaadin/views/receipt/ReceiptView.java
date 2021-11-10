package com.mar.randomvaadin.views.receipt;

import com.mar.randomvaadin.db.entity.Receipt;
import com.mar.randomvaadin.utils.DeleteDialogWidget;
import com.mar.randomvaadin.views.MainView;
import com.mar.randomvaadin.views.product.ProductViewDialog;
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

@RequiredArgsConstructor
public class ReceiptView {

    private final MainView appLayout;

    public VerticalLayout getContent() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        H2 label = new H2("Чё, сколько и зачем потратил");

        List<Receipt> receipts = appLayout.getRepositoryService().getReceiptRepository().findAll();

        Grid<Receipt> grid = new Grid<>();
        grid.addColumn(receipt -> receipt.getProduct().getName()).setHeader("Product");
        grid.addColumn(Receipt::getPrice).setSortable(true).setHeader("Price");
        grid.setItems(receipts);
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.addComponentColumn(receipt -> {
                    DatePicker date = new DatePicker(
                            receipt.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    date.setEnabled(false);
                    return date;
                }
        ).setHeader("Date");
        grid.addComponentColumn(receipt -> {
            Button edtBtn = new Button(new Icon(VaadinIcon.PENCIL), clk -> {
                new UpdateReceiptDialog(appLayout, receipt);
            });
            edtBtn.addThemeVariants(LUMO_TERTIARY);
            Button dltBtn = new Button(new Icon(VaadinIcon.BAN), clk -> {
                new DeleteDialogWidget(() -> {
                    appLayout.getRepositoryService().getReceiptRepository().delete(receipt);
                    appLayout.setContent(appLayout.getReceiptView().getContent());
                });
            });
            dltBtn.addThemeVariants(LUMO_TERTIARY);
            dltBtn.getStyle().set("color", "red");
            return new HorizontalLayout(edtBtn, dltBtn);
        });

        Button crtBtn = new Button(
                "Создать запись",
                new Icon(VaadinIcon.PLUS),
                click -> new CreateReceiptDialog(appLayout)
        );
        crtBtn.setWidthFull();

        HorizontalLayout btns = new HorizontalLayout(
                crtBtn,
                new Button(new Icon(VaadinIcon.COG), click -> new ProductViewDialog(appLayout))
        );

        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, label);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, grid);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, btns);

        verticalLayout.add(label, grid, btns);
        return verticalLayout;
    }
}
