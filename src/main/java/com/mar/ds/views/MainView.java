package com.mar.ds.views;

import com.mar.ds.service.RepositoryService;
import com.mar.ds.views.card.CardView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStream;
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
    private final CardView cardView;
    private final StartPageView startPageView;
    @Getter
    @Autowired
    private RepositoryService repositoryService;
    @Getter
    @Autowired
    private Environment env;

    public MainView() throws IOException {
        cardView = new CardView(this);
        startPageView = new StartPageView(this);

        H1 title = new H1("LibHome");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        Icon icon = VaadinIcon.BOOK.create();
        icon.setSize("40px");
        Anchor anchor = new Anchor();
        anchor.add(icon);
        anchor.setHref("");

        String versions = loadProperties("application.properties").getProperty("version", "1.2.3-DEV.BUILD");
        Label version = new Label(versions);
        version.getStyle().set("font-size", "xx-small");

        HorizontalLayout headTitle = new HorizontalLayout(title, version);
        headTitle.getStyle().set("margin-left", "auto");
        headTitle.getStyle().set("padding", "15px");

        addToNavbar(anchor, headTitle);
        setContent(startPageView.getContent());
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

}
