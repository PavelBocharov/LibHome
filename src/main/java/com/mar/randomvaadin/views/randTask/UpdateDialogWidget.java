package com.mar.randomvaadin.views.randTask;

import com.mar.randomvaadin.db.entity.RandTask;
import com.mar.randomvaadin.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.textfield.TextField;

import static com.vaadin.flow.component.icon.VaadinIcon.PENCIL;

public class UpdateDialogWidget extends Dialog {

    public UpdateDialogWidget(MainView parentLayout, RandTask task) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(true);

        TextField idField = new TextField();
        idField.setWidthFull();
        idField.setAutofocus(false);
        idField.setEnabled(false);
        idField.setLabel("ID");
        idField.setValue(String.valueOf(task.getId()));

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setAutofocus(true);
        textField.setLabel("Текст");
        textField.setValue(task.getText());

        Button updBtn = new Button("Обновить", new Icon(PENCIL));
        updBtn.addClickListener(btnEvent -> {
            task.setText(textField.getValue());
            parentLayout.getRandTaskRepository().save(task);
            updateDialog.close();
            parentLayout.setContent(parentLayout.getRandomTaskView().getContent());
        });
        updBtn.setWidthFull();
        updBtn.setDisableOnClick(true);
        updBtn.addClickShortcut(Key.ENTER);

//        FormLayout formLayout = new FormLayout();
//        formLayout.addFormItem(idField, "ID");
//        formLayout.addFormItem(textField, "Текст");
//        formLayout.add(updBtn);

        updateDialog.add(
                new Text("Обновить задачу"),
                idField,
                textField,
                updBtn
        );
        updateDialog.open();
    }

}
