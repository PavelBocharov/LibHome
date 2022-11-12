package com.mar.ds.views.randTask;

import com.mar.ds.db.entity.RandTask;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.ContentView;
import com.mar.ds.views.MainView;
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
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.mar.ds.utils.ViewUtils.getTextField;
import static com.vaadin.flow.component.icon.VaadinIcon.*;

@RequiredArgsConstructor
public class RandomTaskView implements ContentView {

    private final MainView appLayout;

    private Map<Integer, MutablePair<TextField, HorizontalLayout>> randomData = Collections.synchronizedMap(new HashMap<>());

    public Component getContent() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        verticalLayout.add(new H1("Чё делать сейчас?"));

        List<RandTask> taskList = Collections.emptyList();
        try {
            taskList = appLayout.getRepositoryService().getRandTaskRepository().findAllOrderById(Sort.Direction.ASC);
        } catch (Exception ex) {
            ViewUtils.showErrorMsg("При получении списка задач произошла ошибка", ex, 0);
        }
        randomData.clear();
        Integer randTaskNumber = 1;
        for (RandTask task : taskList) {
            final RandTask randTask = task;
            TextField textField = getTextField(task.getText(), false);

            Button deleteBtn = new Button(new Icon(BAN));
            deleteBtn.getStyle().set("color", "red");
            deleteBtn.addClickListener(delBtn -> {
                new DeleteDialogWidget(() -> {
                    appLayout.getRepositoryService().getRandTaskRepository().delete(randTask);
                    appLayout.setContent(this.getContent());
                });
            });

            // update
            Button updateBtn = new Button(new Icon(PENCIL));
            updateBtn.getStyle().set("color", "green");
            updateBtn.addClickListener(updBtn -> {
                new UpdateDialogWidget(appLayout, task);
            });

            randomData.put(
                    randTaskNumber++,
                    new MutablePair<>(
                            textField,
                            new HorizontalLayout(textField, updateBtn, deleteBtn)
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
