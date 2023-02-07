package com.mar.ds.views.dialog.action;

import com.mar.ds.db.entity.Action;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.views.ContentView;
import com.mar.ds.views.MainView;
import com.mar.ds.views.dialog.teleport.generate.GenerateTypeDialogView;
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

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static com.vaadin.flow.component.icon.VaadinIcon.BAN;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;
import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class ActionView implements ContentView {

    private final MainView appLayout;

    public VerticalLayout getContent() {
        H2 label = new H2("Список ответов в диалогах");
        // TABLE
        Grid<Action> grid = new Grid<>();

        // column
        grid.addColumn(Action::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(action -> appLayout.getRepositoryService().getLocalizationRepository().saveFindRuLocalByKey(action.getText()))
                .setHeader("Текст").setAutoWidth(true);
        grid.addColumn(action -> isNull(action.getNeedItem()) ? "-" : action.getNeedItem().getId())
                .setHeader("Необходимый предмет").setAutoWidth(true);
        grid.addColumn(action -> isNull(action.getNeedMission()) ? "-" : action.getNeedMission().getId())
                .setHeader("Необходимый миссия").setAutoWidth(true);
        grid.addColumn(action -> isNull(action.getNeedTask()) ? "-" : action.getNeedTask().getId())
                .setHeader("Необходимый задача").setAutoWidth(true);
        grid.addColumn(action -> action.getMoveMission() ? '✓' : '✗')
                .setHeader("Двигает миссию дальше").setAutoWidth(true);
        grid.addColumn(action -> action.getIsTeleport() == Boolean.TRUE ? '✓' : '✗')
                .setHeader("Телепорт").setAutoWidth(true);
        grid.addColumn(action -> action.getSaveGame() == Boolean.TRUE ? '✓' : '✗')
                .setHeader("Чекпоинт").setAutoWidth(true);
        grid.addColumn(action -> isNull(action.getGenerateType()) ? '-' : action.getGenerateType().getName())
                .setHeader("Генерирация").setAutoWidth(true);
        grid.addColumn(Action::getLevel).setHeader("Уровень").setAutoWidth(true);
        grid.addColumn(action -> String.format("(%.1f : %.1f : %.1f)", action.getPositionX(), action.getPositionY(), action.getPositionZ()))
                .setHeader("Позиция").setAutoWidth(true);
        grid.addColumn(action -> String.format("(%.1f : %.1f : %.1f)", action.getRotationX(), action.getRotationY(), action.getRotationZ()))
                .setHeader("Поворот").setAutoWidth(true);
        // settings
        grid.setWidthFull();
        // edit
        grid.addItemDoubleClickListener(
                actionItemDoubleClickEvent -> new UpdateActionDialog(appLayout, actionItemDoubleClickEvent.getItem())
        );
        grid.addComponentColumn(action -> {
            Button edtBtn = new Button(new Icon(VaadinIcon.PENCIL), clk -> new UpdateActionDialog(appLayout, action));
            edtBtn.addThemeVariants(LUMO_TERTIARY);
            Button dltBtn = new Button(new Icon(BAN), clk ->
                    new DeleteDialogWidget(() -> {
                        appLayout.getRepositoryService().getActionRepository().delete(action);
                        appLayout.setContent(appLayout.getActionView().getContent());
                    })
            );
            dltBtn.addThemeVariants(LUMO_TERTIARY);
            dltBtn.getStyle().set("color", "red");
            return new HorizontalLayout(edtBtn, dltBtn);
        });

        // value
        List<Action> actions = appLayout.getRepositoryService().getActionRepository().findAll();
        grid.setItems(actions);

        // down buttons
        Button crtBtn = new Button("Добавить ответ/реплику", new Icon(PLUS), click -> new CreateActionDialog(appLayout)
        );
        crtBtn.setWidthFull();
        crtBtn.getStyle().set("color", "green");

        Button generateTypeBtn = new Button(
                "Генериуемые типы локаций",
                new Icon(PLUS), click -> new GenerateTypeDialogView(appLayout)
        );
        generateTypeBtn.setWidthFull();
        generateTypeBtn.getStyle().set("color", "green");

        HorizontalLayout btns = new HorizontalLayout(crtBtn, generateTypeBtn);
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
