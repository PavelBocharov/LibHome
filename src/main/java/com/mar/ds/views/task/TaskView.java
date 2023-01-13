package com.mar.ds.views.task;

import com.mar.ds.db.entity.Task;
import com.mar.ds.db.jpa.TaskRepository;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.views.ContentView;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static com.vaadin.flow.component.icon.VaadinIcon.*;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class TaskView implements ContentView {

    private final MainView appLayout;

    public VerticalLayout getContent() {
        H2 label = new H2("Список задач для миссий");
        // TABLE
        Grid<Task> grid = new Grid<>();

        // column
        grid.addColumn(Task::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(task -> appLayout
                .getRepositoryService()
                .getLocalizationRepository()
                .saveFindRuLocalByKey(task.getText())
        ).setHeader("Text").setAutoWidth(true);
        grid.addColumn(Task::getBeforeId).setHeader("Before").setAutoWidth(true);
        grid.addColumn(Task::getAfterId).setHeader("After").setAutoWidth(true);
        // settings
        grid.setWidthFull();
        // edit
        grid.addItemDoubleClickListener(event -> new UpdateTaskView(appLayout, event.getItem()));
        grid.addComponentColumn(task -> {
            Button edtBtn = new Button(new Icon(PENCIL), clk -> {
                new UpdateTaskView(appLayout, task);
            });
            edtBtn.addThemeVariants(LUMO_TERTIARY);
            Button dltBtn = new Button(new Icon(BAN), clk -> {
                new DeleteDialogWidget(() -> {
                    TaskRepository repository = appLayout.getRepositoryService().getTaskRepository();
                    repository.delete(task);

                    if (nonNull(task.getAfterId())) {
                        Task t = repository.findById(task.getAfterId()).get();
                        t.setBeforeId(null);
                        repository.save(t);
                    }

                    if (nonNull(task.getBeforeId())) {
                        Task t = repository.findById(task.getBeforeId()).get();
                        t.setAfterId(null);
                        repository.save(t);
                    }

                    appLayout.setContent(appLayout.getTaskView().getContent());
                });
            });
            dltBtn.addThemeVariants(LUMO_TERTIARY);
            dltBtn.getStyle().set("color", "red");

            return new HorizontalLayout(edtBtn, dltBtn);
        });

        // value
        List<Task> taskList = appLayout.getRepositoryService().getTaskRepository().findAll();
        grid.setItems(taskList);

        // down buttons
        Button crtBtn = new Button("Добавить задачу", new Icon(PLUS), click -> new CreateTaskView(appLayout));
        crtBtn.setWidthFull();
        crtBtn.getStyle().set("color", "green");

//        Button downloadJson = new Button("Выгрузить JSON", new Icon(DOWNLOAD),
//                click -> {
//                    List<Item> itemList = appLayout.getRepositoryService().getItemRepository().findAll();
//                    List<ItemData> itemDataList = appLayout.getMapperService().getItemMapper().getItemDataList(itemList);
//                    new JSONViewDialog(appLayout, itemDataList);
//                }
//        );
//        downloadJson.setWidthFull();
//        downloadJson.getStyle().set("color", "pink");


        HorizontalLayout btns = new HorizontalLayout(
                crtBtn
//                ,
//                itemTypeListBtn,
//                itemStatusListBtn,
//                downloadJson
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

    public TaskRepository getRepository() {
        return appLayout.getRepositoryService().getTaskRepository();
    }
}
