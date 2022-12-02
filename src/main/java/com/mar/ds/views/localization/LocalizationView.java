package com.mar.ds.views.localization;

import com.mar.ds.db.entity.Localization;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.ContentView;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static com.vaadin.flow.component.icon.VaadinIcon.BAN;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;

@RequiredArgsConstructor
public class LocalizationView implements ContentView {

    private final MainView appLayout;

    public VerticalLayout getContent() {
        H2 label = new H2("Локализация");
        // TABLE
        Grid<Localization> grid = new Grid<>();

        // column
        grid.addColumn(Localization::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Localization::getKey).setHeader("Ключ").setAutoWidth(true);
        grid.addColumn(Localization::getEn).setHeader("EN").setAutoWidth(true);
        grid.addColumn(Localization::getRu).setHeader("RU").setAutoWidth(true);
        // settings
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
//         edit
        grid.addItemDoubleClickListener(
                dialogItemDoubleClickEvent -> new UpdateLocalizationView(appLayout, dialogItemDoubleClickEvent.getItem())
        );
        grid.addComponentColumn(localization -> {
            Button edtBtn = new Button(new Icon(VaadinIcon.PENCIL), clk -> {
                new UpdateLocalizationView(appLayout, localization);
            });
            edtBtn.addThemeVariants(LUMO_TERTIARY);
            Button dltBtn = new Button(new Icon(BAN), clk -> {
                new DeleteDialogWidget(() -> {
                    appLayout.getRepositoryService().getLocalizationRepository().delete(localization);
                    appLayout.setContent(appLayout.getLocalizationView().getContent());
                });
            });
            dltBtn.addThemeVariants(LUMO_TERTIARY);
            dltBtn.getStyle().set("color", "red");
            return new HorizontalLayout(edtBtn, dltBtn);
        });

        // value
        List<Localization> localizationList = appLayout.getRepositoryService().getLocalizationRepository().findAll();
        grid.setItems(localizationList);

        // down buttons
        Button crtBtn = new Button("Добавить локализациюю", new Icon(PLUS), click -> new CreateLocalizationView(appLayout));
        crtBtn.setWidthFull();
        crtBtn.getStyle().set("color", "green");

        HorizontalLayout btns = new HorizontalLayout(
                crtBtn,
                ViewUtils.getDownloadFileButton("Localization.json",
                        appLayout.getMapperService().getLocalizationMapper().getLocalizationDataList(
                                appLayout.getRepositoryService().getLocalizationRepository().findAll()
                        )
                )
        );
        btns.setWidthFull();

        // create view
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, label);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, grid);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, btns);
        verticalLayout.add(label, grid, btns);
        return verticalLayout;
    }
}
