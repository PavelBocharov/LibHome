package com.mar.ds.views.dialog;

import com.mar.ds.db.entity.Action;
import com.mar.ds.db.entity.Dialog;
import com.mar.ds.db.entity.Document;
import com.mar.ds.db.entity.Item;
import com.mar.ds.db.jpa.LocalizationRepository;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.ContentView;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static com.vaadin.flow.component.icon.VaadinIcon.BAN;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class DialogView implements ContentView {
    private final MainView appLayout;

    public VerticalLayout getContent() {
        LocalizationRepository localRepo = appLayout.getRepositoryService().getLocalizationRepository();

        H2 label = new H2("Список диалогов");
        // TABLE
        Grid<Dialog> grid = new Grid<>();

        // column
        grid.addColumn(Dialog::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(dialog -> format("[%d] %.32s", dialog.getCharacter().getId(), localRepo.saveFindRuLocalByKey(dialog.getCharacter().getName())))
                .setHeader("Персонаж").setAutoWidth(true)
        ;
        grid.addColumn(dialog -> format("%.32s", localRepo.saveFindRuLocalByKey(dialog.getText()))).setHeader("Реплика").setAutoWidth(true);
        grid.addColumn(dialog ->
                        isNull(dialog.getItems()) || dialog.getItems().isEmpty()
                                ? "-"
                                : dialog.getItems()
                                .stream()
                                .map(item -> format("[%d] %.32s", item.getId(), localRepo.saveFindRuLocalByKey(item.getName())))
                                .collect(Collectors.joining("; ")))
                .setHeader("Предметы").setAutoWidth(true);

        grid.addColumn(dialog -> isNull(dialog.getActions()) || dialog.getActions().isEmpty()
                        ? "-"
                        : dialog.getActions()
                        .stream()
                        .map(action -> format("[%d] %.32s", action.getId(), localRepo.saveFindRuLocalByKey(action.getText())))
                        .collect(Collectors.joining("; "))
                )
                .setHeader("Действия").setAutoWidth(true);
        // settings
        grid.setWidthFull();
//        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        // edit
        grid.addItemDoubleClickListener(
                dialogItemDoubleClickEvent -> new UpdateDialogView(appLayout, dialogItemDoubleClickEvent.getItem())
        );
        grid.addComponentColumn(dialog -> {
            Button edtBtn = new Button(new Icon(VaadinIcon.PENCIL), clk -> {
                new UpdateDialogView(appLayout, dialog);
            });
//            edtBtn.addThemeVariants(LUMO_TERTIARY);
            Button dltBtn = new Button(new Icon(BAN), clk -> {
                new DeleteDialogWidget(() -> {
                    List<Item> items = dialog.getItems();
                    List<Action> actions = dialog.getActions();
                    List<Document> documents = dialog.getDocuments();
                    List<Action> openingActions = appLayout.getRepositoryService().getActionRepository().findAll();
                    Action openingAction = openingActions.stream()
                            .filter(action -> nonNull(action)
                                    && nonNull(action.getOpenedDialog())
                                    && action.getOpenedDialog().getId().equals(dialog.getId()))
                            .findFirst().orElse(null);
                    if (nonNull(items)) {
                        items.forEach(item -> item.setDialog(null));
                        appLayout.getRepositoryService().getItemRepository().saveAll(items);
                    }
                    if (nonNull(documents)) {
                        documents.forEach(document -> document.setDialog(null));
                        appLayout.getRepositoryService().getDocumentRepository().saveAll(documents);
                    }
                    if (nonNull(actions)) {
                        actions.forEach(action -> action.setDialog(null));
                        appLayout.getRepositoryService().getActionRepository().saveAll(actions);
                    }
                    if (nonNull(openingAction)) {
                        openingAction.setOpenedDialog(null);
                        appLayout.getRepositoryService().getActionRepository().save(openingAction);
                    }

                    appLayout.getRepositoryService().getDialogRepository().delete(dialog);
                    appLayout.setContent(appLayout.getDialogView().getContent());
                });
            });
            dltBtn.addThemeVariants(LUMO_TERTIARY);
            dltBtn.getStyle().set("color", "red");
            return new HorizontalLayout(
                    edtBtn, dltBtn
            );
        });

        // value
        List<Dialog> dialogs = appLayout.getRepositoryService().getDialogRepository().findAll();
        grid.setItems(dialogs);

        // down buttons
        Button crtBtn = new Button("Добавить диалог", new Icon(PLUS), click -> new CreateDialogView(appLayout));
        crtBtn.setWidthFull();
        crtBtn.getStyle().set("color", "green");

        HorizontalLayout btns = new HorizontalLayout(
                crtBtn,
                ViewUtils.getDownloadFileButton(
                        "Dialogs.json",
                        appLayout.getMapperService().getDialogMapper().getDialogDataList(
                                appLayout.getRepositoryService().getDialogRepository().findAll()
                        )
                )
        );
        btns.setWidthFull();

        // create view
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, label);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, grid);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, btns);
        verticalLayout.add(label, grid, btns);
        return verticalLayout;
    }
}
