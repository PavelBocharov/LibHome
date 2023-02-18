package com.mar.ds.views;

import com.mar.ds.db.jpa.LocalizationRepository;
import com.mar.ds.service.MapperService;
import com.mar.ds.service.RepositoryService;
import com.mar.ds.views.character.CharacterView;
import com.mar.ds.views.dialog.DialogView;
import com.mar.ds.views.dialog.action.ActionView;
import com.mar.ds.views.document.DocumentView;
import com.mar.ds.views.item.ItemView;
import com.mar.ds.views.localization.LocalizationView;
import com.mar.ds.views.mission.MissionView;
import com.mar.ds.views.randTask.RandomTaskView;
import com.mar.ds.views.receipt.ReceiptView;
import com.mar.ds.views.task.TaskView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static com.vaadin.flow.component.icon.VaadinIcon.*;

@Route("")
public class MainView extends AppLayout {

    @Getter
    @Autowired
    private RepositoryService repositoryService;

    @Getter
    @Autowired
    private MapperService mapperService;

    @Getter
    private final RandomTaskView randomTaskView;
    @Getter
    private final ReceiptView receiptView;
    @Getter
    private final ItemView itemView;
    @Getter
    private final CharacterView characterView;
    @Getter
    private final TaskView taskView;
    @Getter
    private final MissionView missionView;
    @Getter
    private final ActionView actionView;
    @Getter
    private final DialogView dialogView;
    @Getter
    private final DocumentView documentView;
    @Getter
    private final LocalizationView localizationView;
    private final StartPageView startPageView;

    public MainView() throws IOException {
        randomTaskView = new RandomTaskView(this);
        receiptView = new ReceiptView(this);
        itemView = new ItemView(this);
        characterView = new CharacterView(this);
        taskView = new TaskView(this);
        missionView = new MissionView(this);
        actionView = new ActionView(this);
        dialogView = new DialogView(this);
        documentView = new DocumentView(this);
        localizationView = new LocalizationView(this);
        startPageView = new StartPageView();

        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("'Dark Sun' by MarGS");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(getTab("Стартовая страница", HOME, startPageView));
        tabs.add(getTab("Список дел", RANDOM, randomTaskView));
//        tabs.add(getTab("Схемы", MONEY, receiptView));
        tabs.add(getTab("[DEV] Предметы", FLASK, itemView));
        tabs.add(getTab("[DEV] Пресонажи", CHILD, characterView));
        tabs.add(getTab("[DEV] Задачи", TASKS, taskView));
        tabs.add(getTab("[DEV] Миссии", DIPLOMA_SCROLL, missionView));
        tabs.add(getTab("[DEV] Ответы/реплики", COMMENT, actionView));
        tabs.add(getTab("[DEV] Диалоги", CHAT, dialogView));
        tabs.add(getTab("[DEV] Документы", BOOK, documentView));
        tabs.add(getTab("[DEV] Локализация", TEXT_LABEL, localizationView));

        addToDrawer(tabs);
        addToNavbar(toggle, title);
        setContent(startPageView.getContent());
    }

    private Tab getTab(String title, VaadinIcon icon, ContentView contentView) {
        return new Tab(getButton(title, icon, btnClickEvent -> setContent(contentView.getContent())));
    }

    private Button getButton(String title, VaadinIcon icon, ComponentEventListener<ClickEvent<Button>> listener) {
        Button button = new Button(title, new Icon(icon));
        button.setHeightFull();
        button.addClickListener(listener);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    public LocalizationRepository getLocalRepo() {
        return this.getRepositoryService().getLocalizationRepository();
    }

}
