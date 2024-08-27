package com.mar.ds.views;

import com.mar.ds.service.RepositoryService;
import com.mar.ds.views.card.CardView;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static com.vaadin.flow.component.icon.VaadinIcon.BOOK;
import static com.vaadin.flow.component.icon.VaadinIcon.HOME;

@Route("")
@PageTitle("LibHome")
public class MainView extends AppLayout {

    @Getter
    @Autowired
    private RepositoryService repositoryService;

    @Getter
    private final CardView cardView;
    private final StartPageView startPageView;

    public MainView() throws IOException {
        cardView = new CardView(this);
        startPageView = new StartPageView(this);

//        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("LibHome - your book, game, music and other library.");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-1)")
                .set("margin", "0");
        Icon icon = VaadinIcon.BOOK.create();
        icon.setSize("32px");
        icon.getStyle().set("margin", "3px");

//        Tabs tabs = new Tabs();
//        tabs.setOrientation(Tabs.Orientation.VERTICAL);
//        tabs.add(getTab("Стартовая страница", HOME, startPageView));
//        tabs.add(getTab("Список книг", BOOK, cardView));

//        addToDrawer(tabs);
        addToNavbar(icon, title);
        setContent(startPageView.getContent());
    }

//    private Tab getTab(String title, VaadinIcon icon, ContentView contentView) {
//        return new Tab(getButton(title, icon, btnClickEvent -> setContent(contentView.getContent())));
//    }
//


}
