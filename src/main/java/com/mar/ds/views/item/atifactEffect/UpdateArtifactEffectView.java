package com.mar.ds.views.item.atifactEffect;

import com.mar.ds.db.entity.ArtifactEffect;
import com.mar.ds.utils.ViewUtils;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

import static com.vaadin.flow.component.icon.VaadinIcon.ROTATE_RIGHT;

public class UpdateArtifactEffectView {

    public UpdateArtifactEffectView(ArtifactEffectViewDialog artifactEffectView, ArtifactEffect updatedEntity) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        BigDecimalField numberField = new BigDecimalField();
        numberField.setAutofocus(true);
        numberField.setLabel("Enum number");
        numberField.setWidthFull();
        ViewUtils.setBigDecimalFieldValue(numberField, updatedEntity.getEnumNumber());

        TextField titleField = new TextField();
        titleField.setWidthFull();
        titleField.setLabel("Наименование эффекта");
        ViewUtils.setTextFieldValue(titleField, updatedEntity.getTitle());

        TextField infoField = new TextField();
        infoField.setWidthFull();
        infoField.setLabel("Информация об эффекте");
        ViewUtils.setTextFieldValue(infoField, updatedEntity.getInfo());

        Button updBtn = new Button("Обновить", new Icon(ROTATE_RIGHT));
        updBtn.addClickListener(btnEvent -> {
            try {
                updatedEntity.setEnumNumber(ViewUtils.getLongValue(numberField));
                updatedEntity.setTitle(ViewUtils.getTextFieldValue(titleField));
                updatedEntity.setInfo(ViewUtils.getTextFieldValue(infoField));
                artifactEffectView.getRepository().save(updatedEntity);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                updBtn.setEnabled(true);
                return;
            }
            dialog.close();
            artifactEffectView.reloadData();
        });
        updBtn.setWidthFull();
        updBtn.setDisableOnClick(true);
        updBtn.addClickShortcut(Key.ENTER);

        dialog.add(
                new Label("Обновить эффект"),
                numberField,
                titleField,
                infoField,
                new HorizontalLayout(updBtn, ViewUtils.getCloseButton(dialog))
        );

        dialog.open();
    }

}
