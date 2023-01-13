package com.mar.ds.views.mission;

import com.mar.ds.db.entity.Mission;
import com.mar.ds.db.entity.Task;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.List;
import java.util.stream.Collectors;

import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static com.mar.ds.utils.ViewUtils.setTextFieldValue;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;

public class UpdateMissionView {

    public UpdateMissionView(MainView mainView, Mission updatedMission) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        TextField titleField = new TextField();
        titleField.setWidthFull();
        titleField.setLabel("Название");
        setTextFieldValue(titleField, updatedMission.getTitle());

        TextArea textArea = new TextArea();
        textArea.setWidthFull();
        textArea.setLabel("Описание");
        setTextFieldValue(textArea, updatedMission.getText());

        List<Long> missionsIds = mainView.getRepositoryService().getMissionRepository()
                .findByStartTaskIsNotNull()
                .stream()
                .map(value -> value.getId())
                .collect(Collectors.toList());
        List<Task> taskListTemp = mainView.getTaskView().getRepository().findByBeforeIdIsNull();
        List<Task> taskList = taskListTemp.stream().filter(task -> !missionsIds.contains(task.getId())).collect(Collectors.toList());

        Select<Task> taskSelect = new Select<>();
        taskSelect.setLabel("Стартовая задача");
        taskSelect.setTextRenderer(
                task -> String.format(
                        "%d: %50s",
                        task.getId(),
                        mainView.getRepositoryService().getLocalizationRepository().saveFindRuLocalByKey(task.getText())
                )
        );
        taskSelect.setEmptySelectionAllowed(true);
        taskSelect.setWidthFull();

        if (updatedMission.getStartTask() != null) {
            taskList.add(updatedMission.getStartTask());
        }
        taskSelect.setDataProvider(new ListDataProvider<>(taskList));
        if (updatedMission.getStartTask() != null) taskSelect.setValue(updatedMission.getStartTask());


        Button createBtn = new Button("Обновить", new Icon(PLUS));
        createBtn.addClickListener(btnEvent -> {
            try {
                updatedMission.setTitle(getTextFieldValue(titleField));
                updatedMission.setText(getTextFieldValue(textArea));
                updatedMission.setStartTask(taskSelect.getValue());

                mainView.getMissionView().getRepository().save(updatedMission);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                createBtn.setEnabled(true);
                return;
            }
            createDialog.close();
            mainView.setContent(mainView.getMissionView().getContent());
        });
        createBtn.setWidthFull();
        createBtn.setDisableOnClick(true);
        createBtn.addClickShortcut(Key.ENTER);

        createDialog.add(
                new Label("Обновить миссиию"),
                titleField,
                textArea,
                taskSelect,
                new HorizontalLayout(createBtn, ViewUtils.getCloseButton(createDialog))
        );

        createDialog.open();
    }

}
