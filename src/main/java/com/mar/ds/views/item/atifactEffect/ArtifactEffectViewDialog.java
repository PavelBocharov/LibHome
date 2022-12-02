package com.mar.ds.views.item.atifactEffect;

import com.mar.ds.db.entity.ArtifactEffect;
import com.mar.ds.db.jpa.ArtifactEffectRepository;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.List;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static com.vaadin.flow.component.icon.VaadinIcon.BAN;

public class ArtifactEffectViewDialog {
    private final MainView appLayout;
    private Dialog dialog;
    private Grid<ArtifactEffect> grid;
    private Button crtBtn;

    public ArtifactEffectViewDialog(MainView appLayout) {
        this.appLayout = appLayout;

        dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth(80, Unit.PERCENTAGE);
        dialog.setHeight(80, Unit.PERCENTAGE);


        crtBtn = new Button(new Icon(VaadinIcon.PLUS));
        crtBtn.getStyle().set("color", "green");
        crtBtn.addClickListener(btnClick -> new CreateArtifactEffectView(this));

        reloadData();

        dialog.open();
    }


    private void initProducts() {
        grid = new Grid<>();

        grid.addColumn(ArtifactEffect::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(ArtifactEffect::getEnumNumber).setHeader("U_ID").setAutoWidth(true);
        grid.addColumn(ArtifactEffect::getTitle).setHeader("Title").setAutoWidth(true);
        grid.addColumn(ArtifactEffect::getInfo).setHeader("Info").setAutoWidth(true);

        List<ArtifactEffect> itemStatusList = getRepository().findAll();
        grid.setItems(itemStatusList);

        grid.setWidthFull();
        grid.setHeight(95, Unit.PERCENTAGE);
        // edit
        grid.addItemDoubleClickListener(
                itemItemDoubleClickEvent -> new UpdateArtifactEffectView(this, itemItemDoubleClickEvent.getItem())
        );
        grid.addComponentColumn(item -> {
            Button edtBtn = new Button(new Icon(VaadinIcon.PENCIL), clk -> {
                new UpdateArtifactEffectView(this, item);
            });
            edtBtn.addThemeVariants(LUMO_TERTIARY);
            Button dltBtn = new Button(new Icon(BAN), clk -> {
                new DeleteDialogWidget(() -> {
                    getRepository().delete(item);
                    reloadData();
                });
            });
            dltBtn.addThemeVariants(LUMO_TERTIARY);
            dltBtn.getStyle().set("color", "red");
            return new HorizontalLayout(edtBtn, dltBtn);
        });
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

        Label label = new Label("Эффекты артeфактов");
        label.setWidthFull();
        HorizontalLayout title =
                new HorizontalLayout(
                        label,
                        crtBtn,
                        ViewUtils.getCloseButton(dialog)
                );
        title.setWidthFull();
        dialog.add(title, grid);
    }

    public ArtifactEffectRepository getRepository() {
        return appLayout.getRepositoryService().getArtifactEffectRepository();
    }
}
