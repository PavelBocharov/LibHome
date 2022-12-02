package com.mar.ds.views.item;

import com.mar.ds.db.entity.ArtifactEffect;
import com.mar.ds.db.entity.Item;
import com.mar.ds.db.entity.ItemStatus;
import com.mar.ds.db.entity.ItemType;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.List;

import static com.mar.ds.utils.ViewUtils.getFloatValue;
import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static java.lang.String.format;

public class CreateItemDialog {

    public CreateItemDialog(MainView mainView) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth(80, Unit.PERCENTAGE);
        // name
        TextField name = new TextField("Наименование");
        name.setWidthFull();
        // info
        TextArea info = new TextArea("Информация");
        info.setWidthFull();
        // short info
        TextField shortInfo = new TextField("Краткая информация");
        shortInfo.setWidthFull();
        // status
        List<ItemStatus> itemStatusList = mainView.getRepositoryService().getItemStatusRepository().findAll();
        Select<ItemStatus> itemStatusSelect = new Select<ItemStatus>();
        itemStatusSelect.setLabel("Статус");
        itemStatusSelect.setPlaceholder("Выберите статус...");
        itemStatusSelect.setTextRenderer(itemStatus -> format("[%d] %s", itemStatus.getEnumNumber(), itemStatus.getName()));
        itemStatusSelect.setDataProvider(new ListDataProvider<>(itemStatusList));
        itemStatusSelect.setWidthFull();
        // type
        List<ItemType> itemTypeList = mainView.getRepositoryService().getItemTypeRepository().findAll();
        Select<ItemType> itemTypeSelect = new Select<ItemType>();
        itemTypeSelect.setLabel("Тип");
        itemTypeSelect.setPlaceholder("Выберите тип...");
        itemTypeSelect.setTextRenderer(itemType -> format("[%d] %s", itemType.getEnumNumber(), itemType.getName()));
        itemTypeSelect.setDataProvider(new ListDataProvider<>(itemTypeList));
        itemTypeSelect.setWidthFull();
        // artifactEffect
        List<ArtifactEffect> artifactEffectList = mainView.getRepositoryService().getArtifactEffectRepository().findAll();
        Select<ArtifactEffect> artifactEffectSelect = new Select<ArtifactEffect>();
        artifactEffectSelect.setLabel("Эффект артефакта");
        artifactEffectSelect.setPlaceholder("Выберите эффект...");
        artifactEffectSelect.setTextRenderer(effect -> format("[%d] %s", effect.getEnumNumber(), effect.getTitle()));
        artifactEffectSelect.setDataProvider(new ListDataProvider<>(artifactEffectList));
        artifactEffectSelect.setWidthFull();
        artifactEffectSelect.setEmptySelectionAllowed(true);
        // image path
        TextField imgPath = new TextField("Путь иконки");
        imgPath.setWidthFull();
        // min manna
        BigDecimalField minManna = new BigDecimalField();
        minManna.setLabel("Мин. кол-во манны");
        minManna.setWidthFull();
        // HP DMG
        BigDecimalField hpDmg = new BigDecimalField();
        hpDmg.setLabel("HP DMG");
        hpDmg.setWidthFull();
        // MP DMG
        BigDecimalField mpDmg = new BigDecimalField();
        mpDmg.setLabel("MP DMG");
        mpDmg.setWidthFull();
        // reloadTick
        BigDecimalField reloadTick = new BigDecimalField();
        reloadTick.setLabel("Время отката");
        reloadTick.setWidthFull();
        // obj path
        TextField objPath = new TextField("Путь объекта");
        objPath.setWidthFull();
        // level
        TextField level = new TextField("Уровень");
        level.setWidthFull();
        // position X
        BigDecimalField positionX = new BigDecimalField();
        positionX.setLabel("Position X");
        positionX.setWidthFull();
        // position Y
        BigDecimalField positionY = new BigDecimalField();
        positionY.setLabel("Position Y");
        positionY.setWidthFull();
        // position Z
        BigDecimalField positionZ = new BigDecimalField();
        positionZ.setLabel("Position Z");
        positionZ.setWidthFull();
        // rotation X
        BigDecimalField rotationX = new BigDecimalField();
        rotationX.setLabel("Rotation X");
        rotationX.setWidthFull();
        // rotation X
        BigDecimalField rotationY = new BigDecimalField();
        rotationY.setLabel("Rotation Y");
        rotationY.setWidthFull();
        // rotation X
        BigDecimalField rotationZ = new BigDecimalField();
        rotationZ.setLabel("Rotation Z");
        rotationZ.setWidthFull();


        Button crtBtn = new Button("Создать", new Icon(VaadinIcon.PLUS));
        crtBtn.addClickListener(click -> {
            try {
                Item item = Item.builder()
                        .name(getTextFieldValue(name))
                        .info(getTextFieldValue(info))
                        .shortInfo(getTextFieldValue(shortInfo))
                        .status(itemStatusSelect.getValue())
                        .type(itemTypeSelect.getValue())
                        .artifactEffect(artifactEffectSelect.getValue())
                        .imgPath(getTextFieldValue(imgPath))
                        .needManna(getFloatValue(minManna))
                        .healthDamage(getFloatValue(hpDmg))
                        .mannaDamage(getFloatValue(mpDmg))
                        .reloadTick(getFloatValue(reloadTick))
                        .objPath(getTextFieldValue(objPath))
                        .level(getTextFieldValue(level))
                        .positionX(getFloatValue(positionX))
                        .positionY(getFloatValue(positionY))
                        .positionZ(getFloatValue(positionZ))
                        .rotationX(getFloatValue(rotationX))
                        .rotationY(getFloatValue(rotationY))
                        .rotationZ(getFloatValue(rotationZ))
                        .build();
                mainView.getRepositoryService().getItemRepository().save(item);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
                crtBtn.setEnabled(true);
                return;
            }
            mainView.setContent(mainView.getItemView().getContent());
            dialog.close();
        });
        crtBtn.setWidthFull();
        crtBtn.setDisableOnClick(true);
        crtBtn.addClickShortcut(Key.ENTER);

        dialog.add(
                new Label("Создать новый предмет"),
                name,
                info,
                shortInfo,
                itemStatusSelect,
                itemTypeSelect,
                artifactEffectSelect,
                imgPath,
                minManna,
                hpDmg,
                mpDmg,
                reloadTick,
                objPath,
                level,
                positionX,
                positionY,
                positionZ,
                rotationX,
                rotationY,
                rotationZ,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(dialog))
        );
        dialog.open();
    }
    
}
