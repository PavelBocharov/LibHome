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

import static com.mar.ds.utils.ViewUtils.*;
import static java.lang.String.format;

public class UpdateItemDialog {

    public UpdateItemDialog(MainView mainView, Item updatedItem) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth(80, Unit.PERCENTAGE);
        // name
        TextField name = new TextField("Наименование");
        name.setWidthFull();
        setTextFieldValue(name, updatedItem.getName());
        // info
        TextArea info = new TextArea("Информация");
        info.setWidthFull();
        setTextFieldValue(info, updatedItem.getInfo());
        // short info
        TextField shortInfo = new TextField("Краткая информация");
        shortInfo.setWidthFull();
        setTextFieldValue(shortInfo, updatedItem.getShortInfo());
        // status
        List<ItemStatus> itemStatusList = mainView.getRepositoryService().getItemStatusRepository().findAll();
        Select<ItemStatus> itemStatusSelect = new Select<ItemStatus>();
        itemStatusSelect.setLabel("Статус");
        itemStatusSelect.setPlaceholder("Выберите статус...");
        itemStatusSelect.setTextRenderer(itemStatus -> format("[%d] %s", itemStatus.getEnumNumber(), itemStatus.getName()));
        itemStatusSelect.setDataProvider(new ListDataProvider<>(itemStatusList));
        itemStatusSelect.setWidthFull();
        itemStatusSelect.setValue(updatedItem.getStatus());
        // type
        List<ItemType> itemTypeList = mainView.getRepositoryService().getItemTypeRepository().findAll();
        Select<ItemType> itemTypeSelect = new Select<ItemType>();
        itemTypeSelect.setLabel("Тип");
        itemTypeSelect.setPlaceholder("Выберите тип...");
        itemTypeSelect.setTextRenderer(itemType -> format("[%d] %s", itemType.getEnumNumber(), itemType.getName()));
        itemTypeSelect.setDataProvider(new ListDataProvider<>(itemTypeList));
        itemTypeSelect.setWidthFull();
        itemTypeSelect.setValue(updatedItem.getType());
        // artifactEffect
        List<ArtifactEffect> artifactEffectList = mainView.getRepositoryService().getArtifactEffectRepository().findAll();
        Select<ArtifactEffect> artifactEffectSelect = new Select<ArtifactEffect>();
        artifactEffectSelect.setLabel("Эффект артефакта");
        artifactEffectSelect.setPlaceholder("Выберите эффект...");
        artifactEffectSelect.setTextRenderer(effect -> format("[%d] %s", effect.getEnumNumber(), effect.getTitle()));
        artifactEffectSelect.setDataProvider(new ListDataProvider<>(artifactEffectList));
        artifactEffectSelect.setWidthFull();
        artifactEffectSelect.setEmptySelectionAllowed(true);
        artifactEffectSelect.setValue(updatedItem.getArtifactEffect());
        // image path
        TextField imgPath = new TextField("Путь иконки");
        imgPath.setWidthFull();
        setTextFieldValue(imgPath, updatedItem.getImgPath());
        // min manna
        BigDecimalField minManna = new BigDecimalField();
        minManna.setLabel("Мин. кол-во манны");
        minManna.setWidthFull();
        setBigDecimalFieldValue(minManna, updatedItem.getNeedManna());
        // HP DMG
        BigDecimalField hpDmg = new BigDecimalField();
        hpDmg.setLabel("HP DMG");
        hpDmg.setWidthFull();
        setBigDecimalFieldValue(hpDmg, updatedItem.getHealthDamage());
        // MP DMG
        BigDecimalField mpDmg = new BigDecimalField();
        mpDmg.setLabel("MP DMG");
        mpDmg.setWidthFull();
        setBigDecimalFieldValue(mpDmg, updatedItem.getMannaDamage());
        // reloadTick
        BigDecimalField reloadTick = new BigDecimalField();
        reloadTick.setLabel("Время отката");
        reloadTick.setWidthFull();
        setBigDecimalFieldValue(reloadTick, updatedItem.getReloadTick());
        // obj path
        TextField objPath = new TextField("Путь объекта");
        objPath.setWidthFull();
        setTextFieldValue(objPath, updatedItem.getObjPath());
        // level
        TextField level = new TextField("Уровень");
        level.setWidthFull();
        setTextFieldValue(level, updatedItem.getLevel());
        // position X
        BigDecimalField positionX = new BigDecimalField();
        positionX.setLabel("Position X");
        positionX.setWidthFull();
        setBigDecimalFieldValue(positionX, updatedItem.getPositionX());
        // position Y
        BigDecimalField positionY = new BigDecimalField();
        positionY.setLabel("Position Y");
        positionY.setWidthFull();
        setBigDecimalFieldValue(positionY, updatedItem.getPositionY());
        // position Z
        BigDecimalField positionZ = new BigDecimalField();
        positionZ.setLabel("Position Z");
        positionZ.setWidthFull();
        setBigDecimalFieldValue(positionZ, updatedItem.getPositionZ());
        // rotation X
        BigDecimalField rotationX = new BigDecimalField();
        rotationX.setLabel("Rotation X");
        rotationX.setWidthFull();
        setBigDecimalFieldValue(rotationX, updatedItem.getRotationX());
        // rotation X
        BigDecimalField rotationY = new BigDecimalField();
        rotationY.setLabel("Rotation Y");
        rotationY.setWidthFull();
        setBigDecimalFieldValue(rotationY, updatedItem.getRotationY());
        // rotation X
        BigDecimalField rotationZ = new BigDecimalField();
        rotationZ.setLabel("Rotation Z");
        rotationZ.setWidthFull();
        setBigDecimalFieldValue(rotationZ, updatedItem.getRotationZ());

        Button updBtn = new Button("Обновить", new Icon(VaadinIcon.ROTATE_RIGHT));
        updBtn.addClickListener(click -> {
            try {
                updatedItem.setName(getTextFieldValue(name));
                updatedItem.setInfo(getTextFieldValue(info));
                updatedItem.setShortInfo(getTextFieldValue(shortInfo));
                updatedItem.setStatus(itemStatusSelect.getValue());
                updatedItem.setType(itemTypeSelect.getValue());
                updatedItem.setArtifactEffect(artifactEffectSelect.getValue());
                updatedItem.setImgPath(getTextFieldValue(imgPath));
                updatedItem.setNeedManna(getFloatValue(minManna));
                updatedItem.setHealthDamage(getFloatValue(hpDmg));
                updatedItem.setMannaDamage(getFloatValue(mpDmg));
                updatedItem.setReloadTick(getFloatValue(reloadTick));
                updatedItem.setObjPath(getTextFieldValue(objPath));
                updatedItem.setLevel(getTextFieldValue(level));
                updatedItem.setPositionX(getFloatValue(positionX));
                updatedItem.setPositionY(getFloatValue(positionY));
                updatedItem.setPositionZ(getFloatValue(positionZ));
                updatedItem.setRotationX(getFloatValue(rotationX));
                updatedItem.setRotationY(getFloatValue(rotationY));
                updatedItem.setRotationZ(getFloatValue(rotationZ));
                mainView.getRepositoryService().getItemRepository().save(updatedItem);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                updBtn.setEnabled(true);
                return;
            }
            mainView.setContent(mainView.getItemView().getContent());
            dialog.close();
        });
        updBtn.setWidthFull();
        updBtn.setDisableOnClick(true);
        updBtn.addClickShortcut(Key.ENTER);

        dialog.add(
                new Label("Обновить запись"),
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
                new HorizontalLayout(updBtn, ViewUtils.getCloseButton(dialog))
        );
        dialog.open();
    }

}
