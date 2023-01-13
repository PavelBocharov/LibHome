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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;

public class CreateMissionView {

    public CreateMissionView(MainView mainView) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        TextField titleField = new TextField();
        titleField.setWidthFull();
        titleField.setLabel("Название");

        TextArea textArea = new TextArea();
        textArea.setWidthFull();
        textArea.setLabel("Описание");


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
        taskSelect.setDataProvider(new ListDataProvider<>(taskList));
        taskSelect.setEmptySelectionAllowed(true);
        taskSelect.setWidthFull();


        Button createBtn = new Button("Создать", new Icon(PLUS));
        createBtn.addClickListener(btnEvent -> {
            try {
                String title = getTextFieldValue(titleField);
                String text = getTextFieldValue(textArea);
                Task task = taskSelect.getValue();

                mainView.getMissionView().getRepository().save(
                        Mission.builder()
                                .title(title)
                                .text(text)
                                .startTask(task)
                                .build()
                );
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
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
                new Label("Создать миссию"),
                titleField,
                textArea,
                taskSelect,
                new HorizontalLayout(createBtn, ViewUtils.getCloseButton(createDialog))
        );

        createDialog.open();
    }

}
