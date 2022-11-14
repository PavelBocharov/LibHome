package com.mar.ds.views.dialog;

import com.mar.ds.db.entity.Action;
import com.mar.ds.db.entity.Character;
import com.mar.ds.db.entity.Item;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

public class UpdateDialogView {

    public UpdateDialogView(MainView mainView, com.mar.ds.db.entity.Dialog updatedDialog) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        // name
        TextField textField = new TextField("Текст реплики");
        textField.setWidthFull();
        ViewUtils.setTextFieldValue(textField, updatedDialog.getText());
        // character
        List<Character> characters = mainView.getRepositoryService().getCharacterRepository().findAll();
        Select<Character> characterSelect = new Select<>();
        characterSelect.setLabel("Персонаж");
        characterSelect.setEmptySelectionAllowed(false);
        characterSelect.setPlaceholder("Кто говорит реплику...");
        characterSelect.setTextRenderer(character -> format("%d: %32s", character.getId(), character.getName()));
        characterSelect.setDataProvider(new ListDataProvider<>(characters));
        characterSelect.setWidthFull();
        characterSelect.setValue(updatedDialog.getCharacter());
        // opening dialog
        List<Action> openingActions = mainView.getRepositoryService().getActionRepository().findAll();
        Action oldOpenAction = openingActions.stream()
                .filter(action -> nonNull(action)
                        && nonNull(action.getOpenedDialog())
                        && action.getOpenedDialog().getId().equals(updatedDialog.getId()))
                .findFirst().orElse(null);
        Select<Action> openingActionsSelect = new Select<>();
        openingActionsSelect.setLabel("Открывающая реплика");
        openingActionsSelect.setEmptySelectionAllowed(false);
        openingActionsSelect.setPlaceholder("Выберите реплику...");
        openingActionsSelect.setTextRenderer(action -> format("%d: %32s", action.getId(), action.getText()));
        openingActionsSelect.setDataProvider(new ListDataProvider<>(openingActions));
        openingActionsSelect.setWidthFull();
        openingActionsSelect.setValue(oldOpenAction);
        // items
        List<Item> itemList = mainView.getRepositoryService().getItemRepository().findByDialogIdIsNull();
        List<Item> oldItems = updatedDialog.getItems();
        itemList.addAll(oldItems);
        MultiselectComboBox<Item> itemSelect = new MultiselectComboBox<>();
        itemSelect.setLabel("Предметы, которые получит ГГ");
        itemSelect.setPlaceholder("Выберите предметы...");
        itemSelect.setClearButtonVisible(true);
        itemSelect.setItemLabelGenerator(item -> format("%d: %32s", item.getId(), item.getName()));
        itemSelect.setItems(itemList);
        itemSelect.setWidthFull();
        itemSelect.setValue(new HashSet<>(oldItems));
        // action
        List<Action> actionList = mainView.getRepositoryService().getActionRepository().findByDialogIdIsNull();
        List<Action> oldActions = updatedDialog.getActions();
        actionList.addAll(oldActions);
        MultiselectComboBox<Action> actionSelect = new MultiselectComboBox<>();
        actionSelect.setLabel("Список реплик");
        actionSelect.setPlaceholder("Выберите реплики...");
        actionSelect.setClearButtonVisible(true);
        actionSelect.setItemLabelGenerator(action -> format("%d: %32s", action.getId(), action.getText()));
        actionSelect.setItems(actionList);
        actionSelect.setWidthFull();
        actionSelect.setValue(new HashSet<>(oldActions));

        Button crtBtn = new Button("Обновить", new Icon(VaadinIcon.ROTATE_RIGHT));
        crtBtn.addClickListener(click -> {
            try {
                List<Item> items = new ArrayList<>(itemSelect.getSelectedItems());
                List<Action> actions = new ArrayList<>(actionSelect.getSelectedItems());
                Action openAction = openingActionsSelect.getValue();
                updatedDialog.setText(ViewUtils.getTextFieldValue(textField));
                updatedDialog.setCharacter(characterSelect.getValue());
                updatedDialog.setItems(items);
                updatedDialog.setActions(actions);
                mainView.getRepositoryService().getDialogRepository().save(updatedDialog);

                if (nonNull(items) && !items.isEmpty()) {
                    items.forEach(item -> item.setDialog(updatedDialog));
                    mainView.getRepositoryService().getItemRepository().saveAll(items);
                }
                if (nonNull(oldItems) && !oldItems.isEmpty()) {
                    for (Item oldItem : oldItems) {
                        if (!items.contains(oldItem)) {
                            oldItem.setDialog(null);
                            mainView.getRepositoryService().getItemRepository().save(oldItem);
                        }
                    }
                }
                if (nonNull(actions) && !actions.isEmpty()) {
                    actions.forEach(action -> action.setDialog(updatedDialog));
                    mainView.getRepositoryService().getActionRepository().saveAll(actions);
                }
                if (nonNull(oldActions) && !oldActions.isEmpty()) {
                    for (Action oldAction : oldActions) {
                        if (!actions.contains(oldAction)) {
                            oldAction.setDialog(null);
                            mainView.getRepositoryService().getActionRepository().save(oldAction);
                        }
                    }
                }
                if (nonNull(openAction)) {
                    openAction.setOpenedDialog(updatedDialog);
                    mainView.getRepositoryService().getActionRepository().save(openAction);
                }
                if (!oldOpenAction.getId().equals(openAction.getId())) {
                    oldOpenAction.setOpenedDialog(null);
                    mainView.getRepositoryService().getActionRepository().save(oldOpenAction);
                }
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                crtBtn.setEnabled(true);
                return;
            }
            mainView.setContent(mainView.getDialogView().getContent());
            createDialog.close();
        });
        crtBtn.setWidthFull();
        crtBtn.setDisableOnClick(true);
        crtBtn.addClickShortcut(Key.ENTER);

        createDialog.add(
                new Label("Обновить диалог"),
                textField,
                characterSelect,
                openingActionsSelect,
                itemSelect,
                actionSelect,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(createDialog))
        );
        createDialog.open();
    }

}
