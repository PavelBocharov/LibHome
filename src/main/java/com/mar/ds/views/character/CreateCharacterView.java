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
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;

public class CreateCharacterView {

    public CreateCharacterView(MainView mainView) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        TextField nameField = new TextField();
        nameField.setWidthFull();
        nameField.setLabel("Name");

        TextField portraitField = new TextField();
        portraitField.setWidthFull();
        portraitField.setLabel("Portrait path");

        Button createBtn = new Button("Создать", new Icon(PLUS));
        createBtn.addClickListener(btnEvent -> {
            try {
                String name = getTextFieldValue(nameField);
                String portrait = getTextFieldValue(portraitField);
                mainView.getCharacterView().getRepository().save(Character.builder().name(name).portrait(portrait).build());
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
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
                new Label("Создать персонажа/объект"),
                nameField,
                portraitField,
                new HorizontalLayout(createBtn, ViewUtils.getCloseButton(createDialog))
        );

        createDialog.open();
    }

}
