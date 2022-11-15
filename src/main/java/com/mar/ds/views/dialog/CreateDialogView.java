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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

public class CreateDialogView {

    public CreateDialogView(MainView mainView) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        // name
        TextArea textArea = new TextArea("Текст реплики");
        textArea.setWidthFull();
        // character
        List<Character> characters = mainView.getRepositoryService().getCharacterRepository().findAll();
        Select<Character> characterSelect = new Select<>();
        characterSelect.setLabel("Персонаж");
        characterSelect.setEmptySelectionAllowed(false);
        characterSelect.setPlaceholder("Кто говорит реплику...");
        characterSelect.setTextRenderer(character -> format("%d: %32s", character.getId(), character.getName()));
        characterSelect.setDataProvider(new ListDataProvider<>(characters));
        characterSelect.setWidthFull();
        // opening dialog
        List<Action> openingActions = mainView.getRepositoryService().getActionRepository().findAll();
        Select<Action> openingActionsSelect = new Select<>();
        openingActionsSelect.setLabel("Открывающая реплика");
        openingActionsSelect.setEmptySelectionAllowed(false);
        openingActionsSelect.setPlaceholder("Выберите реплику...");
        openingActionsSelect.setTextRenderer(action -> format("%d: %32s", action.getId(), action.getText()));
        openingActionsSelect.setDataProvider(new ListDataProvider<>(openingActions));
        openingActionsSelect.setWidthFull();
        // items
        List<Item> itemList = mainView.getRepositoryService().getItemRepository().findByDialogIdIsNull();
        MultiselectComboBox<Item> itemSelect = new MultiselectComboBox<>();
        itemSelect.setLabel("Предметы, которые получит ГГ");
        itemSelect.setPlaceholder("Выберите предметы...");
        itemSelect.setClearButtonVisible(true);
        itemSelect.setItemLabelGenerator(item -> format("%d: %32s", item.getId(), item.getName()));
        itemSelect.setItems(itemList);
        itemSelect.setWidthFull();
        // action
        List<Action> actionList = mainView.getRepositoryService().getActionRepository().findByDialogIdIsNull();
        MultiselectComboBox<Action> actionSelect = new MultiselectComboBox<>();
        actionSelect.setLabel("Список реплик");
        actionSelect.setPlaceholder("Выберите реплики...");
        actionSelect.setClearButtonVisible(true);
        actionSelect.setItemLabelGenerator(action -> format("%d: %32s", action.getId(), action.getText()));
        actionSelect.setItems(actionList);
        actionSelect.setWidthFull();


        Button crtBtn = new Button("Создать", new Icon(VaadinIcon.PLUS));
        crtBtn.addClickListener(click -> {
            try {
                List<Item> items = new ArrayList<>(itemSelect.getSelectedItems());
                List<Action> actions = new ArrayList<>(actionSelect.getSelectedItems());
                Action openAction = openingActionsSelect.getValue();
                com.mar.ds.db.entity.Dialog dialog = mainView.getRepositoryService().getDialogRepository()
                        .save(com.mar.ds.db.entity.Dialog.builder()
                                .text(ViewUtils.getTextFieldValue(textArea))
                                .character(characterSelect.getValue())
                                .items(items)
                                .actions(new ArrayList<>())
                                .build());

                if (nonNull(items) && !items.isEmpty()) {
                    items.forEach(item -> item.setDialog(dialog));
                    mainView.getRepositoryService().getItemRepository().saveAll(items);
                }
                if (nonNull(actions) && !actions.isEmpty()) {
                    actions.forEach(action -> action.setDialog(dialog));
                    mainView.getRepositoryService().getActionRepository().saveAll(actions);
                }
                if (nonNull(openAction)) {
                    openAction.setOpenedDialog(dialog);
                    mainView.getRepositoryService().getActionRepository().save(openAction);
                }

            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При создании произошла ошибка", ex);
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
                new Label("Создать новый диалог"),
                textArea,
                characterSelect,
                openingActionsSelect,
                itemSelect,
                actionSelect,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(createDialog))
        );
        createDialog.open();
    }

}
