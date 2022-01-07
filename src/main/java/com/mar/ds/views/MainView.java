package com.mar.ds.views;

import com.mar.ds.service.RepositoryService;
import com.mar.ds.views.randTask.RandomTaskView;
import com.mar.ds.views.receipt.ReceiptView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
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
    private final RandomTaskView randomTaskView;
    @Getter
    private final ReceiptView receiptView;
    private final StartPageView startPageView;

    public MainView() throws IOException {
        randomTaskView = new RandomTaskView(this);
        receiptView = new ReceiptView(this);
        startPageView = new StartPageView();

        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("Dark Sun");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(new Tab(getStartPageBtn()));
        tabs.add(new Tab(getRandomTaskButton()));
        tabs.add(new Tab(getReceiptsBtn()));

        addToDrawer(tabs);
        addToNavbar(toggle, title);
        setContent(startPageView.getContent());
    }

    private Button getRandomTaskButton() {
        Button rndBtn = new Button("Чё делать сейчас?", new Icon(RANDOM));
        rndBtn.setHeightFull();
        rndBtn.addClickListener(buttonClickEvent -> {
            setContent(randomTaskView.getContent());
            this.setDrawerOpened(false);
        });
        rndBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return rndBtn;
    }

    private Button getReceiptsBtn() {
        Button receiptBtn = new Button("Чеки", new Icon(MONEY));
        receiptBtn.setHeightFull();
        receiptBtn.addClickListener(buttonClickEvent -> {
            setContent(receiptView.getContent());
            this.setDrawerOpened(false);
        });
        receiptBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return receiptBtn;
    }

    private Button getStartPageBtn() {
        Button startPage = new Button("Стартовая страница", new Icon(HOME));
        startPage.setHeightFull();
        startPage.addClickListener(buttonClickEvent -> {
            try {
                setContent(startPageView.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.setDrawerOpened(false);
        });
        startPage.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return startPage;
    }

}
