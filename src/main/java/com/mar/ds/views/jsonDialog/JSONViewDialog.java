package com.mar.ds.views.jsonDialog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mar.ds.db.jpa.ItemTypeRepository;
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

    public JSONViewDialog(MainView appLayout, Object objToJson) {
        this.appLayout = appLayout;
        dialog = new Dialog();
        dialog.setMinWidth(50, Unit.PERCENTAGE);
        dialog.setMinHeight(80, Unit.PERCENTAGE);
        try {
            appLayout.setContent(appLayout.getItemView().getContent());

            TextArea jsonArea = new TextArea();
            jsonArea.setSizeFull();
            jsonArea.setValue(new JsonMapper().writeValueAsString(objToJson));

            Label label = new Label("JSON предметов");
            label.setWidthFull();
            Button clsBtn = ViewUtils.getCloseButton(dialog);
            HorizontalLayout horizontalLayout = new HorizontalLayout(label, clsBtn);

            dialog.add(horizontalLayout, jsonArea);
            dialog.open();
        } catch (JsonProcessingException ex) {
            ViewUtils.showErrorMsg("При загрузки JSON-а", ex);
        }
    }

    public ItemTypeRepository getRepository() {
        return appLayout.getRepositoryService().getItemTypeRepository();
    }
}
