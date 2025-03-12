package com.mar.ds.views;

import com.mar.ds.db.service.CardHistoryService;
import com.mar.ds.db.service.CardService;
import com.mar.ds.db.service.CardStatusService;
import com.mar.ds.service.RepositoryService;
import com.mar.ds.utils.FileUtils;
import com.mar.ds.views.card.CardView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Route("")
@PageTitle("LibHome")
@PWA(name = "LibHome",
        shortName = "LibHome",
        description = "LibHome - your book, game, music and other library.",
        iconPath = "icons/icon.png"
)
public class MainView extends AppLayout {

    @Getter
    private final Map<FileUtils.ViewTypeDto, ContentView> cardsView;

    @Getter
    @Autowired
    private RepositoryService repositoryService;

    @Getter
    @Autowired
    private CardService cardService;

    @Getter
    @Autowired
    private CardStatusService cardStatusService;

    @Getter
    @Autowired
    private CardHistoryService cardHistoryService;

    @Getter
    @Autowired
    private Environment env;

    private FileUtils.ViewTypeDto activeView;

    public MainView() throws IOException {
        cardsView = new HashMap<>();

        H3 title = new H3("LibHome");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);

        List<FileUtils.ViewTypeDto> types = FileUtils.getCardViewTypeList(
                loadProperties("application.properties").getProperty("app.data.content.file")
        );
        FileUtils.ViewTypeDto startView = new FileUtils.ViewTypeDto(0, "Start page", null, VaadinIcon.HOME);
        StartPageView startPageView = new StartPageView(this, startView);
        cardsView.put(startView, startPageView);
        tabs.add(getTab(startView.title(), startView.icon(), startPageView));

        for (FileUtils.ViewTypeDto type : types) {
            ContentView view = new CardView(this, type);
            tabs.add(getTab(type.title(), type.icon(), view));
            cardsView.put(type, view);
        }

        String versions = loadProperties("application.properties").getProperty("app.version", "1.2.3-DEV.BUILD");
        Label version = new Label(versions);
        version.getStyle().set("font-size", "xx-small");

        HorizontalLayout headTitle = new HorizontalLayout(title, version);
        headTitle.getStyle().set("margin-left", "auto");
        headTitle.getStyle().set("padding", "15px");

        DrawerToggle toggle = new DrawerToggle();
        addToDrawer(tabs);
        addToNavbar(toggle, headTitle);
        setContentByType(startView);
    }

    public void setContentByType(FileUtils.ViewTypeDto type) {
        activeView = type;
        setContent(getActiveView().getContent());
    }

    public ContentView getActiveView() {
        return cardsView.get(activeView);
    }

    public Properties loadProperties(String resourceFileName) {
        Properties configuration = new Properties();
        try (InputStream inputStream = MainView.class.getClassLoader().getResourceAsStream(resourceFileName)) {
            configuration.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return configuration;
    }

    private Tab getTab(String title, VaadinIcon icon, ContentView contentView) {
        return new Tab(getButton(title, icon, btnClickEvent -> setContentByType(contentView.getViewType())));
    }

    private Button getButton(String title, VaadinIcon icon, ComponentEventListener<ClickEvent<Button>> listener) {
        Button button = new Button(title, icon.create());
        button.setHeightFull();
        button.addClickListener(listener);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    public String getContentJson() {
        return this.getEnv().getProperty("app.data.content.file");
    }
}
