package com.mar.ds.views.task;

import com.mar.ds.db.entity.Task;
import com.mar.ds.db.jpa.LocalizationRepository;
import com.mar.ds.db.jpa.TaskRepository;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.List;
import java.util.stream.Collectors;

import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static com.mar.ds.utils.ViewUtils.setTextFieldValue;
import static com.vaadin.flow.component.icon.VaadinIcon.ROTATE_RIGHT;

public class UpdateTaskView {

    public UpdateTaskView(MainView mainView, Task updatedTask) {
        TaskRepository repository = mainView.getTaskView().getRepository();
        LocalizationRepository localRepo = mainView.getRepositoryService().getLocalizationRepository();

        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setLabel("Text");
        setTextFieldValue(textField, updatedTask.getText());

        List<Task> beforeTaskList = repository.findByAfterIdIsNull()
                .stream()
                .filter(task -> !task.getId().equals(updatedTask.getId()))
                .collect(Collectors.toList());
        Select<Task> beforeTaskSelect = new Select<>();
        beforeTaskSelect.setLabel("Before task");
        beforeTaskSelect.setPlaceholder("Предыдущая задача...");
        beforeTaskSelect.setTextRenderer(task -> String.format("%d: %50s", task.getId(), localRepo.saveFindRuLocalByKey(task.getText())));
        beforeTaskSelect.setEmptySelectionAllowed(true);
        beforeTaskSelect.setWidthFull();

        Task beforeTask = null;
        if (updatedTask.getBeforeId() != null) {
            beforeTask = repository.findById(updatedTask.getBeforeId()).get();
            beforeTaskList.add(beforeTask);
        }
        beforeTaskSelect.setDataProvider(new ListDataProvider<>(beforeTaskList));
        if (beforeTask != null) beforeTaskSelect.setValue(beforeTask);


        List<Task> afterTaskList = mainView.getTaskView().getRepository().findByBeforeIdIsNull()
                .stream()
                .filter(task -> !task.getId().equals(updatedTask.getId()))
                .collect(Collectors.toList());
        Select<Task> afterTaskSelect = new Select<>();
        afterTaskSelect.setLabel("After task");
        afterTaskSelect.setPlaceholder("Слудующая задача...");
        afterTaskSelect.setTextRenderer(task -> String.format("%d: %50s", task.getId(), localRepo.saveFindRuLocalByKey(task.getText())));
        afterTaskSelect.setEmptySelectionAllowed(true);
        afterTaskSelect.setWidthFull();

        Task afterTask = null;
        if (updatedTask.getAfterId() != null) {
            afterTask = repository.findById(updatedTask.getAfterId()).get();
            afterTaskList.add(afterTask);
        }
        afterTaskSelect.setDataProvider(new ListDataProvider<>(afterTaskList));
        if (afterTask != null) afterTaskSelect.setValue(afterTask);

        Button updBtn = new Button("Обновить", new Icon(ROTATE_RIGHT));
        Task finalBeforeTask = beforeTask;
        Task finalAfterTask = afterTask;
        updBtn.addClickListener(btnEvent -> {
            try {
                Task beforeTaskNew = beforeTaskSelect.getValue();
                Task afterTaskNew = afterTaskSelect.getValue();

                updatedTask.setText(getTextFieldValue(textField));
                updatedTask.setBeforeId(beforeTaskNew == null ? null : beforeTaskNew.getId());
                updatedTask.setAfterId(afterTaskNew == null ? null : afterTaskNew.getId());
                Task createdTask = repository.save(updatedTask );

                if (beforeTaskNew != null) { // поменяли на другой
                    if (finalBeforeTask == null) {
                        beforeTaskNew.setAfterId(createdTask.getId());
                        repository.save(beforeTaskNew);
                    } else if (finalBeforeTask.getId() != beforeTaskNew.getId()) {
                        finalBeforeTask.setAfterId(null);
                        repository.save(finalBeforeTask);
                        beforeTaskNew.setAfterId(createdTask.getId());
                        repository.save(beforeTaskNew);
                    }
                } else {
                    if (finalBeforeTask != null) {
                        finalBeforeTask.setAfterId(null);
                        repository.save(finalBeforeTask);
                    }
                }
                if (afterTaskNew != null) {
                    if (finalAfterTask == null) {
                        afterTaskNew.setBeforeId(createdTask.getId());
                        repository.save(afterTaskNew);
                    } else if (finalAfterTask.getId() != afterTaskNew.getId()) {
                        finalAfterTask.setBeforeId(null);
                        repository.save(finalAfterTask);
                        afterTaskNew.setBeforeId(createdTask.getId());
                        repository.save(afterTaskNew);
                    }
                } else {
                    if (finalAfterTask != null) {
                        finalAfterTask.setBeforeId(null);
                        repository.save(finalAfterTask);
                    }
                }
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                updBtn.setEnabled(true);
                return;
            }
            createDialog.close();
            mainView.setContent(mainView.getTaskView().getContent());
        });
        updBtn.setWidthFull();
        updBtn.setDisableOnClick(true);
        updBtn.addClickShortcut(Key.ENTER);

        createDialog.add(
                new Label("Обновить задачу"),
                textField,
                beforeTaskSelect,
                afterTaskSelect,
                new HorizontalLayout(updBtn, ViewUtils.getCloseButton(createDialog))
        );

        createDialog.open();
    }

}
