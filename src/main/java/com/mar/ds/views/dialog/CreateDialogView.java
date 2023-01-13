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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mar.ds.utils.ViewUtils.checkString;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

public class CreateDialogView {

    public CreateDialogView(MainView mainView) {
        Dialog createDialog = new Dialog();
        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(false);

        LocalizationRepository localRepo = mainView.getRepositoryService().getLocalizationRepository();

        // name
        TextArea textArea = new TextArea("Текст реплики");
        textArea.setWidthFull();
        // character
        List<Character> characters = mainView.getRepositoryService().getCharacterRepository().findAll();
        Select<Character> characterSelect = new Select<>();
        characterSelect.setLabel("Персонаж");
        characterSelect.setEmptySelectionAllowed(false);
        characterSelect.setPlaceholder("Кто говорит реплику...");
        characterSelect.setTextRenderer(character -> format("%d: %32s", character.getId(), localRepo.saveFindRuLocalByKey(character.getName())));
        characterSelect.setDataProvider(new ListDataProvider<>(characters));
        characterSelect.setWidthFull();
        // opening dialog
        List<Action> openingActions = mainView.getRepositoryService().getActionRepository().findAll()
                .stream()
                .filter(action -> action.getOpenedDialog() == null)
                .collect(Collectors.toList());
        MultiselectComboBox<Action> openingActionsSelect = new MultiselectComboBox<>();
        openingActionsSelect.setLabel("Открывающая реплика");
        openingActionsSelect.setPlaceholder("Выберите реплику...");
        openingActionsSelect.setItemLabelGenerator(action -> format("%d: %32s", action.getId(), localRepo.saveFindRuLocalByKey(action.getText())));
        openingActionsSelect.setItems(openingActions);
        openingActionsSelect.setWidthFull();
        // items
        List<Item> itemList = mainView.getRepositoryService().getItemRepository().findByDialogIdIsNull();
        MultiselectComboBox<Item> itemSelect = new MultiselectComboBox<>();
        itemSelect.setLabel("Предметы, которые получит ГГ");
        itemSelect.setPlaceholder("Выберите предметы...");
        itemSelect.setClearButtonVisible(true);
        itemSelect.setItemLabelGenerator(item -> format("%d: %32s", item.getId(), localRepo.saveFindRuLocalByKey( item.getName())));
        itemSelect.setItems(itemList);
        itemSelect.setWidthFull();
        // documents
        List<Document> documentList = mainView.getRepositoryService().getDocumentRepository().findByDialogIdIsNull();
        MultiselectComboBox<Document> documentSelect = new MultiselectComboBox<>();
        documentSelect.setLabel("Документы, которые появятся в инвентаре");
        documentSelect.setPlaceholder("Выберите документы...");
        documentSelect.setClearButtonVisible(true);
        documentSelect.setItemLabelGenerator(document -> format("%d: %32s", document.getId(), localRepo.saveFindRuLocalByKey(document.getTitle())));
        documentSelect.setItems(documentList);
        documentSelect.setWidthFull();
        // action
        List<Action> actionList = mainView.getRepositoryService().getActionRepository().findByDialogIdIsNull();
        MultiselectComboBox<Action> actionSelect = new MultiselectComboBox<>();
        actionSelect.setLabel("Список реплик");
        actionSelect.setPlaceholder("Выберите реплики...");
        actionSelect.setClearButtonVisible(true);
        actionSelect.setItemLabelGenerator(action -> format("%d: %32s", action.getId(), localRepo.saveFindRuLocalByKey(action.getText())));
        actionSelect.setItems(actionList);
        actionSelect.setWidthFull();


        Button crtBtn = new Button("Создать", new Icon(VaadinIcon.PLUS));
        crtBtn.addClickListener(click -> {
            try {
                if (checkString(textArea, 50)) {
                    throw new Exception("Некорректно заполнены поля");
                }

                List<Item> items = new ArrayList<>(itemSelect.getSelectedItems());
                List<Document> documents = new ArrayList<>(documentSelect.getSelectedItems());
                List<Action> actions = new ArrayList<>(actionSelect.getSelectedItems());
                Set<Action> openActions = openingActionsSelect.getValue();
                com.mar.ds.db.entity.Dialog dialog = mainView.getRepositoryService().getDialogRepository()
                        .save(com.mar.ds.db.entity.Dialog.builder()
                                .text(ViewUtils.getTextFieldValue(textArea))
                                .character(characterSelect.getValue())
                                .items(items)
                                .documents(documents)
                                .actions(Collections.emptyList())
                                .build());

                if (nonNull(items) && !items.isEmpty()) {
                    items.forEach(item -> item.setDialog(dialog));
                    mainView.getRepositoryService().getItemRepository().saveAll(items);
                }
                if (nonNull(documents) && !documents.isEmpty()) {
                    documents.forEach(document -> document.setDialog(dialog));
                    mainView.getRepositoryService().getDocumentRepository().saveAll(documents);
                }
                if (nonNull(actions) && !actions.isEmpty()) {
                    actions.forEach(action -> action.setDialog(dialog));
                    mainView.getRepositoryService().getActionRepository().saveAll(actions);
                }
                if (nonNull(openActions) && !openActions.isEmpty()) {
                    for (Action openAction : openActions) {
                        openAction.setOpenedDialog(dialog);
                        mainView.getRepositoryService().getActionRepository().save(openAction);
                    }
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
                documentSelect,
                actionSelect,
                new HorizontalLayout(crtBtn, ViewUtils.getCloseButton(createDialog))
        );
        createDialog.open();
    }

}
