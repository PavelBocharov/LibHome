package com.mar.ds.views.character;

import com.mar.ds.db.entity.Character;
import com.mar.ds.db.jpa.CharacterRepository;
import com.mar.ds.utils.DeleteDialogWidget;
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

@RequiredArgsConstructor
public class CharacterView implements ContentView {

    private final MainView appLayout;

    public VerticalLayout getContent() {
        H2 label = new H2("Список внутриигровых предметов");
        // TABLE
        Grid<Character> grid = new Grid<>();

        // column
        grid.addColumn(Character::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Character::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Character::getPortrait).setHeader("Portrait").setAutoWidth(true);
        // settings
        grid.setWidthFull();
        // edit
        grid.addComponentColumn(character -> {
            Button edtBtn = new Button(new Icon(PENCIL), clk -> {
                new UpdateCharacterView(appLayout, character);
            });
            edtBtn.addThemeVariants(LUMO_TERTIARY);
            Button dltBtn = new Button(new Icon(BAN), clk -> {
                new DeleteDialogWidget(() -> {
                    appLayout.getRepositoryService().getCharacterRepository().delete(character);
                    appLayout.setContent(appLayout.getCharacterView().getContent());
                });
            });
            dltBtn.addThemeVariants(LUMO_TERTIARY);
            dltBtn.getStyle().set("color", "red");
            return new HorizontalLayout(edtBtn, dltBtn);
        });

        // value
        List<Character> characterList = appLayout.getRepositoryService().getCharacterRepository().findAll();
        grid.setItems(characterList);

        // down buttons
        Button crtBtn = new Button("Добавить персонажа/объект", new Icon(PLUS), click -> new CreateCharacterView(appLayout));
        crtBtn.setWidthFull();
        crtBtn.getStyle().set("color", "green");

        Button downloadJson = new Button("Выгрузить JSON", new Icon(DOWNLOAD),
                click -> {
                    List<Character> characters = appLayout.getRepositoryService().getCharacterRepository().findAll();
                    new JSONViewDialog("JSON персонажей", appLayout, characters);
                }
        );
        downloadJson.setWidthFull();
        downloadJson.getStyle().set("color", "pink");


        HorizontalLayout btns = new HorizontalLayout(crtBtn, downloadJson);
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

    public CharacterRepository getRepository() {
        return appLayout.getRepositoryService().getCharacterRepository();
    }
}
