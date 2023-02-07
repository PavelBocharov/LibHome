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
import lombok.extern.java.Log;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.mar.ds.utils.ViewUtils.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Log
public class UpdateActionDialog {

    public UpdateActionDialog(MainView mainView, Action updatedAction) {
        Dialog updateDialog = new Dialog();
        updateDialog.setCloseOnEsc(true);
        updateDialog.setCloseOnOutsideClick(false);
        updateDialog.setWidth(50, Unit.PERCENTAGE);

        LocalizationRepository localRepo = mainView.getRepositoryService().getLocalizationRepository();

        // name
        TextField textField = new TextField("Текст реплики");
        textField.setWidthFull();
        setTextFieldValue(textField, updatedAction.getText());
        // items
        List<Item> itemList = mainView.getRepositoryService().getItemRepository().findAll();
        Select<Item> itemSelect = new Select<>();
        itemSelect.setLabel("Необходимый предмет");
        itemSelect.setEmptySelectionAllowed(true);
        itemSelect.setPlaceholder("Выберите предмет...");
        itemSelect.setTextRenderer(item -> String.format("%d: %s", item.getId(), localRepo.saveFindRuLocalByKey(item.getName())));
        itemSelect.setDataProvider(new ListDataProvider<>(itemList));
        itemSelect.setWidthFull();
        setSelectValue(itemSelect, updatedAction.getNeedItem(), itemList);
        // mission
        List<Mission> missionList = mainView.getRepositoryService().getMissionRepository().findAll();
        Select<Mission> missionSelect = new Select<>();
        missionSelect.setLabel("Необходимая миссия");
        missionSelect.setEmptySelectionAllowed(true);
        missionSelect.setPlaceholder("Выберите миссию...");
        missionSelect.setTextRenderer(mission -> String.format("%d: %s", mission.getId(), localRepo.saveFindRuLocalByKey(mission.getTitle())));
        missionSelect.setDataProvider(new ListDataProvider<>(missionList));
        missionSelect.setWidthFull();
        setSelectValue(missionSelect, updatedAction.getNeedMission(), missionList);
        // task
        List<Task> taskList = mainView.getTaskView().getRepository().findAll();
        Select<Task> taskSelect = new Select<>();
        taskSelect.setLabel("Необходимая задача");
        taskSelect.setEmptySelectionAllowed(true);
        taskSelect.setPlaceholder("Выберите задачу...");
        taskSelect.setTextRenderer(task -> String.format("%d: %s", task.getId(), localRepo.saveFindRuLocalByKey(task.getText())));
        taskSelect.setWidthFull();

        if (nonNull(updatedAction.getNeedMission())) {
            List<Task> tempTask = getTaskListByMission(updatedAction.getNeedMission(), taskList);
            taskSelect.setDataProvider(new ListDataProvider<>(tempTask));
            setSelectValue(taskSelect, updatedAction.getNeedTask(), taskList);
        }

        missionSelect.addValueChangeListener(selectMissionComponentValueChangeEvent -> {
            Mission selectedMission = selectMissionComponentValueChangeEvent.getValue();
            taskSelect.setDataProvider(new ListDataProvider<>(getTaskListByMission(selectedMission, taskList)));
        });

        // info
        Checkbox moveMission = new Checkbox("Двигает миссию вперед");
        setCheckbox(moveMission, updatedAction.getMoveMission());

        // isTeleport
        Checkbox isTeleport = new Checkbox("Действие телепортирует в другу локацию");
        setCheckbox(isTeleport, updatedAction.getIsTeleport());
        // saveGame
        Checkbox saveGame = new Checkbox("Автосохрание");
        setCheckbox(saveGame, updatedAction.getSaveGame());
        // generate level
        List<GenerateType> generateTypes = mainView.getRepositoryService().getGenerateTypeRepository().findAll();
        Select<GenerateType> generateTypesSelect = new Select<>();
        generateTypesSelect.setLabel("Генерируемая локация");
        generateTypesSelect.setEmptySelectionAllowed(true);
        generateTypesSelect.setPlaceholder("Тип локации...");
        generateTypesSelect.setTextRenderer(type -> String.format("%d: %s", type.getEnumNumber(), type.getName()));
        generateTypesSelect.setDataProvider(new ListDataProvider<>(generateTypes));
        generateTypesSelect.setWidthFull();
        ViewUtils.setSelectValue(generateTypesSelect, updatedAction.getGenerateType(), generateTypes);
        // level
        TextField level = new TextField("Уровень");
        level.setWidthFull();
        setTextFieldValue(level, updatedAction.getLevel());
        // position X
        BigDecimalField positionX = new BigDecimalField();
        positionX.setLabel("Position X");
        positionX.setWidthFull();
        setBigDecimalFieldValue(positionX, updatedAction.getPositionX());
        // position Y
        BigDecimalField positionY = new BigDecimalField();
        positionY.setLabel("Position Y");
        positionY.setWidthFull();
        setBigDecimalFieldValue(positionY, updatedAction.getPositionY());
        // position Z
        BigDecimalField positionZ = new BigDecimalField();
        positionZ.setLabel("Position Z");
        positionZ.setWidthFull();
        setBigDecimalFieldValue(positionZ, updatedAction.getPositionZ());
        // rotation X
        BigDecimalField rotationX = new BigDecimalField();
        rotationX.setLabel("Rotation X");
        rotationX.setWidthFull();
        setBigDecimalFieldValue(rotationX, updatedAction.getRotationX());
        // rotation X
        BigDecimalField rotationY = new BigDecimalField();
        rotationY.setLabel("Rotation Y");
        rotationY.setWidthFull();
        setBigDecimalFieldValue(rotationY, updatedAction.getRotationY());
        // rotation X
        BigDecimalField rotationZ = new BigDecimalField();
        rotationZ.setLabel("Rotation Z");
        rotationZ.setWidthFull();
        setBigDecimalFieldValue(rotationZ, updatedAction.getRotationZ());



        Button updBtn = new Button("Обновить", new Icon(VaadinIcon.ROTATE_RIGHT));
        updBtn.addClickListener(click -> {
            try {
                updatedAction.setText(getTextFieldValue(textField));
                updatedAction.setNeedItem(itemSelect.getValue());
                updatedAction.setNeedMission(missionSelect.getValue());
                updatedAction.setNeedTask(taskSelect.getValue());
                updatedAction.setMoveMission(moveMission.getValue());

                updatedAction.setIsTeleport(isTeleport.getValue());
                updatedAction.setSaveGame(saveGame.getValue());
                updatedAction.setLevel(getTextFieldValue(level));
                updatedAction.setPositionX(getFloatValue(positionX));
                updatedAction.setPositionY(getFloatValue(positionY));
                updatedAction.setPositionZ(getFloatValue(positionZ));
                updatedAction.setRotationX(getFloatValue(rotationX));
                updatedAction.setRotationY(getFloatValue(rotationY));
                updatedAction.setRotationZ(getFloatValue(rotationZ));
                updatedAction.setGenerateType(generateTypesSelect.getValue());

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

        Accordion accordion = new Accordion();
        accordion.setWidthFull();

        accordion.add("Основное", getAccordionContent(textField));
        accordion.add("Предметы", getAccordionContent(itemSelect));
        accordion.add("Миссии и задачи", getAccordionContent(missionSelect, taskSelect, moveMission));
        accordion.add("Телепорт", getAccordionContent(isTeleport, saveGame, generateTypesSelect, level, positionX, positionY, positionZ, rotationX, rotationY, rotationZ));

        updateDialog.add(
                new Label("Обновить ответ/реплику"),
                accordion,
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
