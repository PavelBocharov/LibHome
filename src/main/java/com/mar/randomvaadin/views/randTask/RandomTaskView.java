package com.mar.randomvaadin.views.randTask;

import com.mar.randomvaadin.db.entity.RandTask;
import com.mar.randomvaadin.views.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.mar.randomvaadin.utils.ViewUtils.getTextField;
import static com.vaadin.flow.component.icon.VaadinIcon.*;

@RequiredArgsConstructor
public class RandomTaskView {

    private final MainView appLayout;

    private Map<Integer, MutablePair<TextField, HorizontalLayout>> randomData = Collections.synchronizedMap(new HashMap<>());

    public Component getContent() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        verticalLayout.add(new H1("Чё делать сейчас?"));

        List<RandTask> taskList = appLayout.getRandTaskRepository().findAll();
        randomData.clear();
        for (RandTask task : taskList) {
            final RandTask randTask = task;
            TextField textField = getTextField(task.getText(), false);
            Button deleteBtn = new Button(new Icon(DEL_A));
            deleteBtn.addClickListener(delBtn -> {
                appLayout.getRandTaskRepository().delete(randTask);
                appLayout.setContent(this.getContent());
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
        createTaskButton.addClickListener(buttonClickEvent ->
                new CreateDialogWidget(appLayout)
        );

        HorizontalLayout horizontalLayoutButton = new HorizontalLayout();
        horizontalLayoutButton.add(rndBtn, createTaskButton);

        verticalLayout.add(horizontalLayoutButton);
        return verticalLayout;
    }

}
