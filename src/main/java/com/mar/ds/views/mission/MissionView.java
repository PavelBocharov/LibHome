package com.mar.ds.views.mission;

import com.mar.ds.db.entity.Mission;
import com.mar.ds.db.jpa.LocalizationRepository;
import com.mar.ds.db.jpa.MissionRepository;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.utils.jsonDialog.JSONViewDialog;
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
public class MissionView implements ContentView {

    private final MainView appLayout;

    public VerticalLayout getContent() {
        H2 label = new H2("Список миссий");
        // TABLE
        Grid<Mission> grid = new Grid<>();

        LocalizationRepository localRepo = appLayout.getRepositoryService().getLocalizationRepository();
        // column
        grid.addColumn(Mission::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(mission -> localRepo.saveFindRuLocalByKey(mission.getTitle())).setHeader("Заголовок").setAutoWidth(true);
        grid.addColumn(mission -> localRepo.saveFindRuLocalByKey(mission.getText())).setHeader("Описание").setAutoWidth(true);
        grid.addColumn(mission -> nonNull(mission.getStartTask()) ? mission.getStartTask().getId() : "")
                .setHeader("Первое задание").setAutoWidth(true);
        // settings
        grid.setWidthFull();
        // edit
        grid.addItemDoubleClickListener(event -> new UpdateMissionView(appLayout, event.getItem()));
        grid.addComponentColumn(mission -> {
            Button edtBtn = new Button(new Icon(PENCIL), clk -> {
                new UpdateMissionView(appLayout, mission);
            });
            edtBtn.addThemeVariants(LUMO_TERTIARY);
            Button dltBtn = new Button(new Icon(BAN), clk -> {
                new DeleteDialogWidget(() -> {
                    getRepository().delete(mission);
                    appLayout.setContent(appLayout.getMissionView().getContent());
                });
            });
            dltBtn.addThemeVariants(LUMO_TERTIARY);
            dltBtn.getStyle().set("color", "red");

            return new HorizontalLayout(edtBtn, dltBtn);
        });

        // value
        List<Mission> missionList = getRepository().findAll();
        grid.setItems(missionList);

        // down buttons
        Button crtBtn = new Button("Добавить миссию", new Icon(PLUS), click -> new CreateMissionView(appLayout));
        crtBtn.setWidthFull();
        crtBtn.getStyle().set("color", "green");

        Button downloadJson = new Button("Выгрузить JSON", new Icon(DOWNLOAD),
                click -> {
                    List<Mission> missions = appLayout.getRepositoryService().getMissionRepository().findAll();
                    try {
                        new JSONViewDialog(
                                "JSON миссий",
                                appLayout,
                                appLayout.getMapperService().getMissionMapper().getMissionData(
                                        missions,
                                        appLayout.getRepositoryService().getTaskRepository()
                                )
                        );
                    } catch (Exception e) {
                        ViewUtils.showErrorMsg("При создании JSON произошла ошибка", e);
                        e.printStackTrace();
                    }
                }
        );
        downloadJson.setWidthFull();
        downloadJson.getStyle().set("color", "pink");

        HorizontalLayout btns = new HorizontalLayout(
                crtBtn,
                ViewUtils.getDownloadFileButton(
                        "Mission.json",
                        appLayout.getMapperService().getMissionMapper().getMissionData(
                                appLayout.getRepositoryService().getMissionRepository().findAll(),
                                appLayout.getRepositoryService().getTaskRepository()
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

    public MissionRepository getRepository() {
        return appLayout.getRepositoryService().getMissionRepository();
    }
}
