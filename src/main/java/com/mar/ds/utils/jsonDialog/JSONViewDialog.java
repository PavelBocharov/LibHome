package com.mar.ds.utils.jsonDialog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;

public class JSONViewDialog {

    private final MainView appLayout;
    private Dialog dialog;

    public JSONViewDialog(String title, MainView appLayout, Object objToJson) {
        this.appLayout = appLayout;
        dialog = new Dialog();
        dialog.setMinWidth(50, Unit.PERCENTAGE);
        dialog.setMinHeight(80, Unit.PERCENTAGE);
        try {
            TextArea jsonArea = new TextArea();
            jsonArea.setWidthFull();
            jsonArea.setValue(new JsonMapper()
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .writeValueAsString(objToJson));

            Label label = new Label(title);
            label.setWidthFull();
            Button clsBtn = ViewUtils.getCloseButton(dialog);
            HorizontalLayout horizontalLayout = new HorizontalLayout(label, clsBtn);

            dialog.add(horizontalLayout, jsonArea);
            dialog.open();
        } catch (JsonProcessingException ex) {
            ViewUtils.showErrorMsg("При загрузки JSON-а", ex);
        }
    }
}
