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

import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;

public class CreateArtifactEffectView {

    public CreateArtifactEffectView(ArtifactEffectViewDialog artifactEffectView) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        BigDecimalField numberField = new BigDecimalField();
        numberField.setAutofocus(true);
        numberField.setLabel("Enum number");
        numberField.setWidthFull();

        TextField titleField = new TextField();
        titleField.setWidthFull();
        titleField.setLabel("Наименование эффекта");

        TextField infoField = new TextField();
        infoField.setWidthFull();
        infoField.setLabel("Информация об эффекте");

        Button createBtn = new Button("Создать", new Icon(PLUS));
        createBtn.addClickListener(btnEvent -> {
            try {
                artifactEffectView.getRepository()
                        .save(
                                ArtifactEffect.builder()
                                        .enumNumber(ViewUtils.getLongValue(numberField))
                                        .title(ViewUtils.getTextFieldValue(titleField))
                                        .info(ViewUtils.getTextFieldValue(infoField))
                                        .build()
                        );
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
                createBtn.setEnabled(true);
                return;
            }
            createDialog.close();
            artifactEffectView.reloadData();
        });
        createBtn.setWidthFull();
        createBtn.setDisableOnClick(true);
        createBtn.addClickShortcut(Key.ENTER);

        createDialog.add(
                new Label("Создать новый эффект"),
                numberField,
                titleField,
                infoField,
                new HorizontalLayout(createBtn, ViewUtils.getCloseButton(createDialog))
        );

        createDialog.open();
    }

}
