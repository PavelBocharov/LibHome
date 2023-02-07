package com.mar.ds.views.dialog.action;

import com.mar.ds.db.entity.*;
import com.mar.ds.db.jpa.LocalizationRepository;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.mar.ds.utils.ViewUtils.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class CreateActionDialog {

    public CreateActionDialog(MainView mainView) {
        Dialog createDialog = new Dialog();
        createDialog.setWidth(50, Unit.PERCENTAGE);
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        LocalizationRepository localRepo = mainView.getRepositoryService().getLocalizationRepository();

        Accordion accordion = new Accordion();
        accordion.setWidthFull();

        // name
        TextField textField = new TextField("Текст реплики");
        textField.setWidthFull();
        // items
        List<Item> itemList = mainView.getRepositoryService().getItemRepository().findAll();
        Select<Item> itemSelect = new Select<>();
        itemSelect.setLabel("Необходимый предмет");
        itemSelect.setEmptySelectionAllowed(true);
        itemSelect.setPlaceholder("Выберите предмет...");
        itemSelect.setTextRenderer(item -> String.format("%d: %s", item.getId(), localRepo.saveFindRuLocalByKey(item.getName())));
        itemSelect.setDataProvider(new ListDataProvider<>(itemList));
        itemSelect.setWidthFull();
        // mission
        List<Mission> missionList = mainView.getRepositoryService().getMissionRepository().findAll();
        Select<Mission> missionSelect = new Select<>();
        missionSelect.setLabel("Необходимая миссия");
        missionSelect.setEmptySelectionAllowed(true);
        missionSelect.setPlaceholder("Выберите миссию...");
        missionSelect.setTextRenderer(mission -> String.format("%d: %s", mission.getId(), localRepo.saveFindRuLocalByKey(mission.getTitle())));
        missionSelect.setDataProvider(new ListDataProvider<>(missionList));
        missionSelect.setWidthFull();
        // task
        List<Task> taskList = mainView.getTaskView().getRepository().findAll();
        Select<Task> taskSelect = new Select<>();
        taskSelect.setLabel("Необходимая задача");
        taskSelect.setEmptySelectionAllowed(true);
        taskSelect.setPlaceholder("Выберите задачу...");
        taskSelect.setTextRenderer(task -> String.format("%d: %s", task.getId(), localRepo.saveFindRuLocalByKey(task.getText())));
        taskSelect.setWidthFull();

        missionSelect.addValueChangeListener(selectMissionComponentValueChangeEvent -> {
            Mission selectedMission = selectMissionComponentValueChangeEvent.getValue();
            taskSelect.setDataProvider(new ListDataProvider<>(getTaskListByMission(selectedMission, taskList)));
        });

        // info
        Checkbox moveMission = new Checkbox("Двигает миссию вперед");

        // isTeleport
        Checkbox isTeleport = new Checkbox("Действие телепортирует в другую локацию");
        // saveGame
        Checkbox saveGame = new Checkbox("Автосохрание");
        // generate level
        List<GenerateType> generateTypes = mainView.getRepositoryService().getGenerateTypeRepository().findAll();
        Select<GenerateType> generateTypesSelect = new Select<>();
        generateTypesSelect.setLabel("Генерируемая локация");
        generateTypesSelect.setEmptySelectionAllowed(true);
        generateTypesSelect.setPlaceholder("Тип локации...");
        generateTypesSelect.setTextRenderer(type -> String.format("%d: %s", type.getEnumNumber(), type.getName()));
        generateTypesSelect.setDataProvider(new ListDataProvider<>(generateTypes));
        generateTypesSelect.setWidthFull();

        // level
        TextField level = new TextField("Уровень");
        level.setWidthFull();
        // position X
        BigDecimalField positionX = new BigDecimalField();
        positionX.setLabel("Position X");
        positionX.setWidthFull();
        // position Y
        BigDecimalField positionY = new BigDecimalField();
        positionY.setLabel("Position Y");
        positionY.setWidthFull();
        // position Z
        BigDecimalField positionZ = new BigDecimalField();
        positionZ.setLabel("Position Z");
        positionZ.setWidthFull();
        // rotation X
        BigDecimalField rotationX = new BigDecimalField();
        rotationX.setLabel("Rotation X");
        rotationX.setWidthFull();
        // rotation X
        BigDecimalField rotationY = new BigDecimalField();
        rotationY.setLabel("Rotation Y");
        rotationY.setWidthFull();
        // rotation X
        BigDecimalField rotationZ = new BigDecimalField();
        rotationZ.setLabel("Rotation Z");
        rotationZ.setWidthFull();

        accordion.add("Основное", getAccordionContent(textField));
        accordion.add("Предметы", getAccordionContent(itemSelect));
        accordion.add("Миссии и задачи", getAccordionContent(missionSelect, taskSelect, moveMission));
        accordion.add("Телепорт", getAccordionContent(isTeleport, saveGame, generateTypesSelect, level, positionX, positionY, positionZ, rotationX, rotationY, rotationZ));

        Button crtBtn = new Button("Создать", new Icon(VaadinIcon.PLUS));
        crtBtn.addClickListener(click -> {
            try {
                Action action = Action.builder()
                        .text(getTextFieldValue(textField))
                        .needItem(itemSelect.getValue())
                        .needMission(missionSelect.getValue())
                        .needTask(taskSelect.getValue())
                        .moveMission(moveMission.getValue())
                        .isTeleport(isTeleport.getValue())
                        .saveGame(saveGame.getValue())
                        .level(getTextFieldValue(level))
                        .positionX(getFloatValue(positionX))
                        .positionY(getFloatValue(positionY))
                        .positionZ(getFloatValue(positionZ))
                        .rotationX(getFloatValue(rotationX))
                        .rotationY(getFloatValue(rotationY))
                        .rotationZ(getFloatValue(rotationZ))
                        .generateType(generateTypesSelect.getValue())
                        .build();
                mainView.getRepositoryService().getActionRepository().save(action);
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
                crtBtn.setEnabled(true);
                return;
            }
            mainView.setContent(mainView.getActionView().getContent());
            createDialog.close();
        });
        crtBtn.setWidthFull();
        crtBtn.setDisableOnClick(true);
        crtBtn.addClickShortcut(Key.ENTER);

        createDialog.add(
                new Label("Создать новый ответ/реплику"),
                accordion,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(createDialog))
        );
        createDialog.open();
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
