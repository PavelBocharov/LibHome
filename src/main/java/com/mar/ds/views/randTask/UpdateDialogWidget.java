package com.mar.ds.views.randTask;

import com.mar.ds.db.entity.RandTask;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import static com.vaadin.flow.component.icon.VaadinIcon.PENCIL;

public class UpdateDialogWidget extends Dialog {

    public UpdateDialogWidget(MainView parentLayout, RandTask task) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);

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
            try {
                parentLayout.getRepositoryService().getRandTaskRepository().save(task);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                updBtn.setEnabled(true);
                return;
            }
            updateDialog.close();
            parentLayout.setContent(parentLayout.getRandomTaskView().getContent());
        });
        updBtn.setWidthFull();
        updBtn.setDisableOnClick(true);
        updBtn.addClickShortcut(Key.ENTER);

        updateDialog.add(
                new Text("Обновить задачу"),
                idField,
                textField,
                new HorizontalLayout(updBtn, ViewUtils.getCloseButton(updateDialog))
        );
        updateDialog.open();
    }

}
