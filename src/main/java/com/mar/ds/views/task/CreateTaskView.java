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

import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;

public class CreateTaskView {

    public CreateTaskView(MainView mainView) {
        LocalizationRepository localRepo = mainView.getRepositoryService().getLocalizationRepository();

        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setLabel("Text");

        List<Task> beforeTaskList = mainView.getTaskView().getRepository().findByAfterIdIsNull();
        Select<Task> beforeTaskSelect = new Select<>();
        beforeTaskSelect.setLabel("Before task");
        beforeTaskSelect.setPlaceholder("Предыдущая задача...");
        beforeTaskSelect.setTextRenderer(task -> String.format("%d: %50s", task.getId(), localRepo.saveFindRuLocalByKey(task.getText())));
        beforeTaskSelect.setDataProvider(new ListDataProvider<>(beforeTaskList));
        beforeTaskSelect.setWidthFull();

        List<Task> afterTaskList = mainView.getTaskView().getRepository().findByBeforeIdIsNull();
        Select<Task> afterTaskSelect = new Select<>();
        afterTaskSelect.setLabel("After task");
        afterTaskSelect.setPlaceholder("Слудующая задача...");
        afterTaskSelect.setTextRenderer(task -> String.format("%d: %50s", task.getId(), localRepo.saveFindRuLocalByKey(task.getText())));
        afterTaskSelect.setDataProvider(new ListDataProvider<>(afterTaskList));
        afterTaskSelect.setWidthFull();

        Button createBtn = new Button("Создать", new Icon(PLUS));
        createBtn.addClickListener(btnEvent -> {
            try {
                String textValue = getTextFieldValue(textField);
                Task beforeTask = beforeTaskSelect.getValue();
                Task afterTask = afterTaskSelect.getValue();

                TaskRepository repository = mainView.getTaskView().getRepository();
                Task createdTask = repository.save(
                        Task.builder()
                                .text(textValue)
                                .beforeId(beforeTask == null ? null : beforeTask.getId())
                                .afterId(afterTask == null ? null : afterTask.getId())
                                .build()
                );

                if (beforeTask != null) {
                    beforeTask.setAfterId(createdTask.getId());
                    repository.save(beforeTask);
                }
                if (afterTask != null) {
                    afterTask.setBeforeId(createdTask.getId());
                    repository.save(afterTask);
                }
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
                createBtn.setEnabled(true);
                return;
            }
            createDialog.close();
            mainView.setContent(mainView.getTaskView().getContent());
        });
        createBtn.setWidthFull();
        createBtn.setDisableOnClick(true);
        createBtn.addClickShortcut(Key.ENTER);

        createDialog.add(
                new Label("Создать задачу"),
                textField,
                beforeTaskSelect,
                afterTaskSelect,
                new HorizontalLayout(createBtn, ViewUtils.getCloseButton(createDialog))
        );

        createDialog.open();
    }

}
