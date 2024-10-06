package com.mar.ds.views;

import com.mar.ds.db.entity.ViewType;
import com.mar.ds.service.RepositoryService;
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
    private final Map<ViewType, ContentView> cardsView;

    @Getter
    @Autowired
    private RepositoryService repositoryService;

    @Getter
    @Autowired
    private Environment env;

    private ViewType activeView;

    public MainView() throws IOException {
        cardsView = new HashMap<>();

        H3 title = new H3("LibHome");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        for (ViewType type : ViewType.values()) {
            ContentView view;
            if (ViewType.NULL.equals(type)) {
                view = new StartPageView(this, ViewType.NULL);
            } else {
                view = new CardView(this, type);
            }
            tabs.add(getTab(type.getTitle(), type.getIcon(), view));
            cardsView.put(type, view);
        }

        String versions = loadProperties("application.yml").getProperty("version", "1.2.3-DEV.BUILD");
        Label version = new Label(versions);
        version.getStyle().set("font-size", "xx-small");

        HorizontalLayout headTitle = new HorizontalLayout(title, version);
        headTitle.getStyle().set("margin-left", "auto");
        headTitle.getStyle().set("padding", "15px");

        DrawerToggle toggle = new DrawerToggle();
        addToDrawer(tabs);
        addToNavbar(toggle, headTitle);
        setContentByType(ViewType.NULL);
    }

    public void setContentByType(ViewType type) {
        activeView = type;
        reloadContent();
    }

    public void reloadContent() {
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
}
