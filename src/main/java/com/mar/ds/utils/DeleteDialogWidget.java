package com.mar.ds.utils;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import static com.vaadin.flow.component.icon.VaadinIcon.BAN;
import static com.vaadin.flow.component.icon.VaadinIcon.CHECK;

public class DeleteDialogWidget extends Dialog {

    public DeleteDialogWidget(Runnable deleteEvent) {
        Dialog deleteDialog = new Dialog();
        deleteDialog.setCloseOnEsc(true);
        deleteDialog.setCloseOnOutsideClick(false);

        Button yesBtn = new Button("Удалить", new Icon(CHECK));
        yesBtn.getStyle().set("color", "red");
        yesBtn.addClickListener(btnEvent -> {
            try {
                deleteEvent.run();
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При удалении произошла ошибка", ex);
            }
            deleteDialog.close();
        });

        Button noBtn = new Button("Отмена", new Icon(BAN));
        noBtn.addClickListener(btnEvent -> {
            deleteDialog.close();
        });

        deleteDialog.add(new Text("Удалить запись?"), new HorizontalLayout(yesBtn, noBtn));
        deleteDialog.open();
    }

}
