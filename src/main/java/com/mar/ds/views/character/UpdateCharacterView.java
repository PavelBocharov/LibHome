package com.mar.ds.views.character;

import com.mar.ds.db.entity.Character;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static com.mar.ds.utils.ViewUtils.setTextFieldValue;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;
import static com.vaadin.flow.component.icon.VaadinIcon.ROTATE_RIGHT;

public class UpdateCharacterView {

    public UpdateCharacterView(MainView mainView, Character updatedCharacter) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        TextField nameField = new TextField();
        nameField.setWidthFull();
        nameField.setLabel("Name");
        setTextFieldValue(nameField, updatedCharacter.getName());

        TextField portraitField = new TextField();
        portraitField.setWidthFull();
        portraitField.setLabel("Portrait path");
        setTextFieldValue(portraitField, updatedCharacter.getPortrait());

        Button createBtn = new Button("Обновить", new Icon(ROTATE_RIGHT));
        createBtn.addClickListener(btnEvent -> {
            try {
                updatedCharacter.setName(getTextFieldValue(nameField));
                updatedCharacter.setPortrait(getTextFieldValue(portraitField));

                mainView.getCharacterView().getRepository().save(updatedCharacter);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                createBtn.setEnabled(true);
                return;
            }
            createDialog.close();
            mainView.setContent(mainView.getCharacterView().getContent());
        });
        createBtn.setWidthFull();
        createBtn.setDisableOnClick(true);
        createBtn.addClickShortcut(Key.ENTER);

        createDialog.add(
                new Label("Обновить персонажа/объект"),
                nameField,
                portraitField,
                new HorizontalLayout(createBtn, ViewUtils.getCloseButton(createDialog))
        );

        createDialog.open();
    }

}
