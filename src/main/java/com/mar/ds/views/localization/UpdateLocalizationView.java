package com.mar.ds.views.localization;

import com.mar.ds.db.entity.Localization;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import static com.mar.ds.utils.ViewUtils.*;

public class UpdateLocalizationView {

    public UpdateLocalizationView(MainView mainView, Localization updatedEntity) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        // key
        TextField keyText = new TextField("Ключ");
        keyText.setRequired(true);
        keyText.setWidthFull();
        setTextFieldValue(keyText, updatedEntity.getKey());
        // en
        TextArea enText = new TextArea("English");
        enText.setRequired(true);
        enText.setWidthFull();
        setTextFieldValue(enText, updatedEntity.getEn());
        // ru
        TextArea ruText = new TextArea("Русский");
        ruText.setRequired(true);
        ruText.setWidthFull();
        setTextFieldValue(ruText, updatedEntity.getRu());

        Button actionBtn = new Button("Обновить", new Icon(VaadinIcon.ROTATE_RIGHT));
        actionBtn.addClickListener(click -> {
            try {
                if (checkString(ruText, 80) || checkString(enText, 80)) {
                    throw new Exception("Некорректно заполнены поля");
                }
                updatedEntity.setKey(getTextFieldValue(keyText));
                updatedEntity.setEn(getTextFieldValue(enText));
                updatedEntity.setRu(getTextFieldValue(ruText));

                mainView.getRepositoryService().getLocalizationRepository().save(updatedEntity);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                actionBtn.setEnabled(true);
                return;
            }
            mainView.setContent(mainView.getLocalizationView().getContent());
            dialog.close();
        });
        actionBtn.setWidthFull();
        actionBtn.setDisableOnClick(true);
        actionBtn.addClickShortcut(Key.ENTER);

        dialog.add(
                new Label("Обновить локализацию"),
                keyText,
                enText,
                ruText,
                new HorizontalLayout(actionBtn, ViewUtils.getCloseButton(dialog))
        );
        dialog.open();
    }

}
