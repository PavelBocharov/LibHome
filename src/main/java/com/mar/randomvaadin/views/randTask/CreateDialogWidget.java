package com.mar.randomvaadin.views.randTask;

import com.mar.randomvaadin.db.entity.RandTask;
import com.mar.randomvaadin.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.textfield.TextField;

import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;

public class CreateDialogWidget extends Dialog {

    public CreateDialogWidget(MainView parentLayout) {
        Dialog createDialog = new Dialog();

        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(true);

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setAutofocus(true);
        textField.setLabel("Текст");

        Button createBtn = new Button("Создать", new Icon(PLUS));
        createBtn.addClickListener(btnEvent -> {
            String text = textField.getValue();
            parentLayout.getRandTaskRepository().save(new RandTask(text));
            createDialog.close();
            parentLayout.setContent(parentLayout.getRandomTaskView().getContent());
        });
        createBtn.setWidthFull();
        createBtn.setDisableOnClick(true);
        createBtn.addClickShortcut(Key.ENTER);

        createDialog.add(
                new Text("Создать новую задачу"),
                textField,
                createBtn
        );
        createDialog.open();
    }

}
