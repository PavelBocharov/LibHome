package com.mar.ds.views.dialog;

import com.mar.ds.db.entity.Action;
import com.mar.ds.db.entity.Character;
import com.mar.ds.db.entity.Document;
import com.mar.ds.db.entity.Item;
import com.mar.ds.db.jpa.LocalizationRepository;
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
import com.vaadin.flow.data.provider.ListDataProvider;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mar.ds.utils.ViewUtils.checkString;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class UpdateDialogView {

    public UpdateDialogView(MainView mainView, com.mar.ds.db.entity.Dialog updatedDialog) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        LocalizationRepository localRepo = mainView.getRepositoryService().getLocalizationRepository();

        // name
        TextArea textArea = new TextArea("Текст реплики");
        textArea.setWidthFull();
        ViewUtils.setTextFieldValue(textArea, updatedDialog.getText());
        // character
        List<Character> characters = mainView.getRepositoryService().getCharacterRepository().findAll();
        Select<Character> characterSelect = new Select<>();
        characterSelect.setLabel("Персонаж");
        characterSelect.setEmptySelectionAllowed(false);
        characterSelect.setPlaceholder("Кто говорит реплику...");
        characterSelect.setTextRenderer(character -> format("%d: %32s", character.getId(), localRepo.saveFindRuLocalByKey(character.getName())));
        characterSelect.setDataProvider(new ListDataProvider<>(characters));
        characterSelect.setWidthFull();
        characterSelect.setValue(updatedDialog.getCharacter());
        // opening dialog
        List<Action> openingActions = mainView.getRepositoryService().getActionRepository().findAll();
        Set<Action> oldOpenActions = openingActions.stream()
                .filter(action -> nonNull(action)
                        && nonNull(action.getOpenedDialog())
                        && action.getOpenedDialog().getId().equals(updatedDialog.getId()))
                .collect(Collectors.toSet());
        openingActions = openingActions.stream().filter(action -> action.getOpenedDialog() == null).collect(Collectors.toList());
        openingActions.addAll(oldOpenActions);
        MultiselectComboBox<Action> openingActionsSelect = new MultiselectComboBox<>();
        openingActionsSelect.setLabel("Открывающая реплика");
        openingActionsSelect.setPlaceholder("Выберите реплику...");
        openingActionsSelect.setItemLabelGenerator(action -> format("%d: %32s", action.getId(), localRepo.saveFindRuLocalByKey(action.getText())));
        openingActionsSelect.setItems(openingActions);
        openingActionsSelect.setWidthFull();
        openingActionsSelect.setValue(oldOpenActions);
        // items
        List<Item> itemList = mainView.getRepositoryService().getItemRepository().findByDialogIdIsNull();
        List<Item> oldItems = updatedDialog.getItems();
        itemList.addAll(oldItems);
        MultiselectComboBox<Item> itemSelect = new MultiselectComboBox<>();
        itemSelect.setLabel("Предметы, которые получит ГГ");
        itemSelect.setPlaceholder("Выберите предметы...");
        itemSelect.setClearButtonVisible(true);
        itemSelect.setItemLabelGenerator(item -> format("%d: %32s", item.getId(), localRepo.saveFindRuLocalByKey(item.getName())));
        itemSelect.setItems(itemList);
        itemSelect.setWidthFull();
        itemSelect.setValue(new HashSet<>(oldItems));
        // documents
        List<Document> documentList = mainView.getRepositoryService().getDocumentRepository().findByDialogIdIsNull();
        List<Document> oldDocs = updatedDialog.getDocuments();
        documentList.addAll(oldDocs);
        MultiselectComboBox<Document> documentSelect = new MultiselectComboBox<>();
        documentSelect.setLabel("Документы, которые появятся в инвентаре");
        documentSelect.setPlaceholder("Выберите документы...");
        documentSelect.setClearButtonVisible(true);
        documentSelect.setItemLabelGenerator(document -> format("%d: %32s", document.getId(), localRepo.saveFindRuLocalByKey(document.getTitle())));
        documentSelect.setItems(documentList);
        documentSelect.setWidthFull();
        documentSelect.setValue(new HashSet<>(oldDocs));
        // action
        List<Action> actionList = mainView.getRepositoryService().getActionRepository().findByDialogIdIsNull();
        List<Action> oldActions = updatedDialog.getActions();
        actionList.addAll(oldActions);
        MultiselectComboBox<Action> actionSelect = new MultiselectComboBox<>();
        actionSelect.setLabel("Список реплик");
        actionSelect.setPlaceholder("Выберите реплики...");
        actionSelect.setClearButtonVisible(true);
        actionSelect.setItemLabelGenerator(action -> format("%d: %32s", action.getId(), localRepo.saveFindRuLocalByKey(action.getText())));
        actionSelect.setItems(actionList);
        actionSelect.setWidthFull();
        actionSelect.setValue(new HashSet<>(oldActions));

        Button crtBtn = new Button("Обновить", new Icon(VaadinIcon.ROTATE_RIGHT));
        crtBtn.addClickListener(click -> {
            try {
                if (checkString(textArea, 50)) {
                    throw new RuntimeException("Некорректно заполнены поля");
                }

                List<Item> items = new ArrayList<>(itemSelect.getSelectedItems());
                List<Document> documents = new ArrayList<>(documentSelect.getSelectedItems());
                List<Action> actions = new ArrayList<>(actionSelect.getSelectedItems());
                Set<Action> openActions = openingActionsSelect.getValue();
                updatedDialog.setText(ViewUtils.getTextFieldValue(textArea));
                updatedDialog.setCharacter(characterSelect.getValue());
                updatedDialog.setItems(items);
                updatedDialog.setDocuments(documents);
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
                if (nonNull(documents) && !documents.isEmpty()) {
                    documents.forEach(document -> document.setDialog(updatedDialog));
                    mainView.getRepositoryService().getDocumentRepository().saveAll(documents);
                }
                if (nonNull(oldDocs) && !oldDocs.isEmpty()) {
                    for (Document oldDoc : oldDocs) {
                        if (!documents.contains(oldDoc)) {
                            oldDoc.setDialog(null);
                            mainView.getRepositoryService().getDocumentRepository().save(oldDoc);
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
                if (nonNull(openActions) && !openActions.isEmpty()) {
                    for (Action openAction : openActions) {
                        openAction.setOpenedDialog(updatedDialog);
                        mainView.getRepositoryService().getActionRepository().save(openAction);
                    }
                }
                if (nonNull(oldOpenActions) && !oldOpenActions.isEmpty()) {
                    if (nonNull(openActions) && !openActions.isEmpty()) {
                        Set<Long> newActionIds = oldOpenActions.stream().map(action -> action.getId()).collect(Collectors.toSet());
                        for (Action oldOpenAction : oldOpenActions) {
                            if (isNull(oldOpenAction) || !newActionIds.contains(oldOpenAction.getId())) {
                                oldOpenAction.setOpenedDialog(null);
                                mainView.getRepositoryService().getActionRepository().save(oldOpenAction);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ViewUtils.showErrorMsg("При обновлении произошла ошибка", ex);
                crtBtn.setEnabled(true);
                ex.printStackTrace();
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
                textArea,
                characterSelect,
                openingActionsSelect,
                itemSelect,
                documentSelect,
                actionSelect,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(createDialog))
        );
        createDialog.open();
    }

}
