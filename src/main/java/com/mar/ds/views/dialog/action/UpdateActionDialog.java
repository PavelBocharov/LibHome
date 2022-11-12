package com.mar.ds.views.dialog.action;

import com.mar.ds.db.entity.Action;
import com.mar.ds.db.entity.Item;
import com.mar.ds.db.entity.Mission;
import com.mar.ds.db.entity.Task;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static com.mar.ds.utils.ViewUtils.setTextFieldValue;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class UpdateActionDialog {

    public UpdateActionDialog(MainView mainView, Action updatedAction) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);

        // name
        TextField textField = new TextField("Тест реплики");
        textField.setWidthFull();
        setTextFieldValue(textField, updatedAction.getText());
        // items
        List<Item> itemList = mainView.getRepositoryService().getItemRepository().findAll();
        Select<Item> itemSelect = new Select<Item>();
        itemSelect.setLabel("Необходимый предмет");
        itemSelect.setEmptySelectionAllowed(true);
        itemSelect.setPlaceholder("Выберите предмет...");
        itemSelect.setTextRenderer(item -> String.format("%d: %s", item.getId(), item.getName()));
        itemSelect.setDataProvider(new ListDataProvider<>(itemList));
        itemSelect.setWidthFull();
        itemSelect.setValue(updatedAction.getNeedItem());
        // mission
        List<Mission> missionList = mainView.getRepositoryService().getMissionRepository().findAll();
        Select<Mission> missionSelect = new Select<Mission>();
        missionSelect.setLabel("Необходимая миссия");
        missionSelect.setEmptySelectionAllowed(true);
        missionSelect.setPlaceholder("Выберите миссию...");
        missionSelect.setTextRenderer(mission -> String.format("%d: %s", mission.getId(), mission.getTitle()));
        missionSelect.setDataProvider(new ListDataProvider<>(missionList));
        missionSelect.setWidthFull();
        missionSelect.setValue(updatedAction.getNeedMission());
        // task
        List<Task> taskList = mainView.getTaskView().getRepository().findAll();
        Select<Task> taskSelect = new Select<Task>();
        taskSelect.setLabel("Необходимая задача");
        taskSelect.setEmptySelectionAllowed(true);
        taskSelect.setPlaceholder("Выберите задачу...");
        taskSelect.setTextRenderer(task -> String.format("%d: %s", task.getId(), task.getText()));
        taskSelect.setWidthFull();

        if (nonNull(updatedAction.getNeedMission())) {
            List<Task> tempTask = getTaskListByMission(updatedAction.getNeedMission(), taskList);
            taskSelect.setDataProvider(new ListDataProvider<>(tempTask));
            if (nonNull(updatedAction.getNeedTask())) {
                Task selectedTask = tempTask.stream()
                        .filter(task -> task.getId().equals(updatedAction.getNeedTask().getId()))
                        .findFirst()
                        .get();
                taskSelect.setValue(selectedTask);
            }
        }

        missionSelect.addValueChangeListener(selectMissionComponentValueChangeEvent -> {
            Mission selectedMission = selectMissionComponentValueChangeEvent.getValue();
            taskSelect.setDataProvider(new ListDataProvider<>(getTaskListByMission(selectedMission, taskList)));
        });

        // info
        Checkbox moveMission = new Checkbox("Двигает миссию вперед");
        moveMission.setValue(updatedAction.getMoveMission());

        Button updBtn = new Button("Обновить", new Icon(VaadinIcon.ROTATE_RIGHT));
        updBtn.addClickListener(click -> {
            try {
                updatedAction.setText(getTextFieldValue(textField));
                updatedAction.setNeedItem(itemSelect.getValue());
                updatedAction.setNeedMission(missionSelect.getValue());
                updatedAction.setNeedTask(taskSelect.getValue());
                updatedAction.setMoveMission(moveMission.getValue());
                mainView.getRepositoryService().getActionRepository().save(updatedAction);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                updBtn.setEnabled(true);
                return;
            }
            mainView.setContent(mainView.getActionView().getContent());
            updateDialog.close();
        });
        updBtn.setWidthFull();
        updBtn.setDisableOnClick(true);
        updBtn.addClickShortcut(Key.ENTER);

        updateDialog.add(
                new Label("Обновить ответ/реплику"),
                textField,
                itemSelect,
                missionSelect,
                taskSelect,
                moveMission,
                new HorizontalLayout(updBtn, ViewUtils.getCloseButton(updateDialog))
        );
        updateDialog.open();
    }

    private List<Task> getTaskListByMission(Mission selectedMission, List<Task> taskList) {
        if (isNull(selectedMission)) return Collections.emptyList();

        List<Task> res = new LinkedList<>();
        if (nonNull(selectedMission.getStartTask())) {
            res.add(selectedMission.getStartTask());
            Long nextTaskId = selectedMission.getStartTask().getAfterId();
            while (nonNull(nextTaskId)) {

                Long finalNextTaskId = nextTaskId;
                Task nextTask = taskList.stream()
                        .filter(task -> nonNull(task) ? task.getId().equals(finalNextTaskId) : null)
                        .findFirst()
                        .orElse(null);


                if (nonNull(nextTask)) {
                    res.add(nextTask);
                    nextTaskId = nextTask.getAfterId();
                } else {
                    nextTaskId = null;
                }
            }
        }

        return res;
    }

}
