package com.mar.ds.views.item;

import com.mar.ds.db.entity.Item;
import com.mar.ds.db.jpa.LocalizationRepository;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.ContentView;
import com.mar.ds.views.MainView;
import com.mar.ds.views.item.atifactEffect.ArtifactEffectViewDialog;
import com.mar.ds.views.item.itemStatus.ItemStatusViewDialog;
import com.mar.ds.views.item.itemType.ItemTypeViewDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static com.vaadin.flow.component.icon.VaadinIcon.*;
import static java.lang.String.format;
import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class ItemView implements ContentView {

    private final MainView appLayout;

    public VerticalLayout getContent() {
        H2 label = new H2("Список внутриигровых предметов");
        // TABLE
        Grid<Item> grid = new Grid<>();

        LocalizationRepository localRepo = appLayout.getRepositoryService().getLocalizationRepository();

        // column
        grid.addColumn(Item::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(item -> localRepo.saveFindRuLocalByKey(item.getName()))
                .setHeader("Наименование").setAutoWidth(true);
        grid.addColumn(item -> item.getStatus().getName()).setHeader("Статус").setAutoWidth(true);
        grid.addColumn(item -> item.getType().getName()).setHeader("Тип").setAutoWidth(true);
        grid.addColumn(item -> isNull(item.getArtifactEffect())
                ? "-"
                : item.getArtifactEffect().getTitle()
        ).setHeader("Эффект").setAutoWidth(true);
        grid.addColumn(Item::getLevel).setHeader("Уровень").setAutoWidth(true);
        grid.addColumn(Item::getNeedManna).setHeader("Мин. кол-во манны").setAutoWidth(true);
        grid.addColumn(Item::getHealthDamage).setHeader("HP DMG").setAutoWidth(true);
        grid.addColumn(Item::getMannaDamage).setHeader("MP DMG").setAutoWidth(true);
        grid.addColumn(Item::getReloadTick).setHeader("Время отката").setAutoWidth(true);
        grid.addColumn(item -> format("(%.1f : %.1f : %.1f)", item.getPositionX(), item.getPositionY(), item.getPositionZ()))
                .setHeader("Spawn point").setAutoWidth(true);
        grid.addColumn(item -> format("(%.1f : %.1f : %.1f)", item.getRotationX(), item.getRotationY(), item.getRotationZ()))
                .setHeader("Rotation").setAutoWidth(true);
        grid.addColumn(Item::getImgPath).setHeader("Путь иконки").setAutoWidth(true);
        grid.addColumn(Item::getObjPath).setHeader("Путь объекта").setAutoWidth(true);
        grid.addColumn(item -> format("%.32s", localRepo.saveFindRuLocalByKey(item.getShortInfo()))).setAutoWidth(true).setHeader("Краткая информация");
        grid.addColumn(item -> format("%.32s", localRepo.saveFindRuLocalByKey(item.getInfo()))).setAutoWidth(true).setHeader("Информация");
        // settings
        grid.setWidthFull();
//        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        // edit
        grid.addItemDoubleClickListener(
                itemItemDoubleClickEvent -> new UpdateItemDialog(appLayout, itemItemDoubleClickEvent.getItem())
        );
        grid.addComponentColumn(item -> {
            Button edtBtn = new Button(new Icon(VaadinIcon.PENCIL), clk -> {
                new UpdateItemDialog(appLayout, item);
            });
            edtBtn.addThemeVariants(LUMO_TERTIARY);
            Button dltBtn = new Button(new Icon(BAN), clk -> {
                new DeleteDialogWidget(() -> {
                    appLayout.getRepositoryService().getItemRepository().delete(item);
                    appLayout.setContent(appLayout.getItemView().getContent());
                });
            });
            dltBtn.addThemeVariants(LUMO_TERTIARY);
            dltBtn.getStyle().set("color", "red");
            return new HorizontalLayout(
                    edtBtn,
                    dltBtn
            );
        });

        // value
        List<Item> items = appLayout.getRepositoryService().getItemRepository().findAll();
        grid.setItems(items);

        // down buttons
        Button crtBtn = new Button("Добавить предмет", new Icon(PLUS), click -> new CreateItemDialog(appLayout));
        crtBtn.setWidthFull();
        crtBtn.getStyle().set("color", "green");

        Button itemTypeListBtn = new Button("Типы предметов", new Icon(COG_O), click -> new ItemTypeViewDialog(appLayout));
        itemTypeListBtn.setWidthFull();

        Button itemStatusListBtn = new Button("Статус предметов", new Icon(COG), click -> new ItemStatusViewDialog(appLayout));
        itemStatusListBtn.setWidthFull();

        Button artifactEffectBtn = new Button("Эффекты артeфактов", new Icon(MAGIC), click -> new ArtifactEffectViewDialog(appLayout));
        artifactEffectBtn.setWidthFull();

        HorizontalLayout btns = new HorizontalLayout(
                crtBtn,
                itemTypeListBtn,
                itemStatusListBtn,
                artifactEffectBtn,
                ViewUtils.getDownloadFileButton(
                        "Items.json",
                        appLayout.getMapperService().getItemMapper().getItemDataList(
                                appLayout.getRepositoryService().getItemRepository().findAll()
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
