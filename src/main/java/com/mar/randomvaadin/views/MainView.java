package com.mar.randomvaadin.views;

import com.mar.randomvaadin.db.entity.RandTask;
import com.mar.randomvaadin.db.jpa.RandTaskRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.mar.randomvaadin.utils.ViewUtils.getImageByResource;
import static com.vaadin.flow.component.icon.VaadinIcon.*;

@Route("")
public class MainView extends AppLayout {

    @Autowired
    private RandTaskRepository randTaskJpaCRUD;

    private Map<Integer, MutablePair<TextField, HorizontalLayout>> randomData = new HashMap<>();

    public MainView() throws IOException {
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
                    this.setDrawerOpened(false);
                });
        rndBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        tabs.add(new Tab(rndBtn));

        addToDrawer(tabs);
        addToNavbar(toggle, title);

        Image image = getImageByResource("static/img/vmu-01.png");
        image.setWidthFull();
        image.setMaxWidth(600.0f, Unit.PIXELS);

        VerticalLayout verticalLayout = new VerticalLayout(
                new H3("Тут всякий хлам который должен упростить жЫзнЪ"),
                image
        );
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        setContent(verticalLayout);
    }



    private VerticalLayout getRandomView() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        verticalLayout.add(new H1("Чё делать сейчас?"));

        List<RandTask> taskList = randTaskJpaCRUD.findAll();
        randomData.clear();
        for (RandTask task : taskList) {
            final RandTask randTask = task;
            TextField textField = getLabelTextField(task.getText());
            Button deleteBtn = new Button(new Icon(DEL_A));
            deleteBtn.addClickListener(delBtn -> {
                randTaskJpaCRUD.delete(randTask);
                setContent(getRandomView());
            });
            randomData.put(
                    task.getNumber(),
                    new MutablePair<>(
                            textField,
                            new HorizontalLayout(textField, deleteBtn)
                    )
            );
        }

        for (Integer i : randomData.keySet().stream().sorted(Integer::compareTo).collect(Collectors.toList())) {
            verticalLayout.add(randomData.get(i).getValue());
        }

        Icon icon = new Icon(RANDOM);
        Button rndBtn = new Button("Крутить барабан", icon);
        rndBtn.addClickListener(buttonClickEvent -> {
            int randomNum =  ThreadLocalRandom.current().nextInt(1, randomData.size() + 1);

            for (Integer i : randomData.keySet()) {
                randomData.get(i).getKey().setInvalid(false);
            }

            TextField rndTF = randomData.get(randomNum).getKey();
            rndTF.setInvalid(true);
        });
        rndBtn.setEnabled(randomData.size() > 0);

        Button createTaskButton = new Button(new Icon(PLUS));
        createTaskButton.addClickListener(buttonClickEvent -> createNewRandTask());

        HorizontalLayout horizontalLayoutButton = new HorizontalLayout();
        horizontalLayoutButton.add(rndBtn, createTaskButton);

        verticalLayout.add(horizontalLayoutButton);
        return verticalLayout;
    }

    private void createNewRandTask() {
        Dialog createDialog = new Dialog();

        createDialog.setCloseOnEsc(true);
        createDialog.setCloseOnOutsideClick(true);

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setAutofocus(true);

        Button createBtn = new Button("Создать", new Icon(PLUS));
        createBtn.addClickListener(btnEvent -> {
                String text = textField.getValue();
                randTaskJpaCRUD.create(text);
                createDialog.close();
                setContent(getRandomView());
        });
        createBtn.setWidthFull();
        createBtn.setDisableOnClick(true);
        createBtn.addClickShortcut(Key.ENTER);

        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(textField, "Текст");
        formLayout.add(createBtn);

        createDialog.add(
                new Text("Создать новую задачу"),
                formLayout,
                createBtn
        );
        createDialog.open();
    }

    private TextField getLabelTextField(String text) {
        TextField textField = new TextField();
        textField.setEnabled(false);
        textField.setValue(text);
        return textField;
    }

}
