package com.mar.randomvaadin.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.vaadin.flow.component.icon.VaadinIcon.RANDOM;

@Route("")
//public class MainView extends VerticalLayout {
public class MainView extends AppLayout {

    private Map<Integer, TextField> randomData = new HashMap<>();

    public MainView() {
        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("My test Vaadin");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);

        Button rndBtn = new Button("Чё делать сейчас?", new Icon(RANDOM));
        rndBtn.setHeightFull();
        rndBtn.addClickListener(buttonClickEvent -> {
                    setContent(getRandomView());
                });
        rndBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        tabs.add(new Tab(rndBtn));

        addToDrawer(tabs);
        addToNavbar(toggle, title);
    }

    private VerticalLayout getRandomView() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        verticalLayout.add(new H1("Чё делать сейчас?"));

        randomData.put(1, getLabelTextField("Разработка"));
        randomData.put(2, getLabelTextField("Чтение"));
        randomData.put(3, getLabelTextField("Рисование"));
        randomData.put(4, getLabelTextField("Тексты"));

        for (Integer i : randomData.keySet().stream().sorted(Integer::compareTo).collect(Collectors.toList())) {
            verticalLayout.add(randomData.get(i));
        }

        Icon icon = new Icon(RANDOM);
        Button rndBtn = new Button("Крутить барабан", icon);
        rndBtn.addClickListener(buttonClickEvent -> {
            int randomNum =  ThreadLocalRandom.current().nextInt(1, randomData.size() + 1);

            for (Integer i : randomData.keySet()) {
                randomData.get(i).setInvalid(false);
            }

            TextField rndTF = randomData.get(randomNum);
            rndTF.setInvalid(true);
        });
        verticalLayout.add(rndBtn);
        return verticalLayout;
    }

    private TextField getLabelTextField(String text) {
        TextField textField = new TextField();
        textField.setEnabled(false);
        textField.setValue(text);
        return textField;
    }

}
