package com.mar.ds.views;

import com.mar.ds.utils.ViewUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;

import static com.mar.ds.utils.ViewUtils.getImageByResource;

@RequiredArgsConstructor
public class StartPageView implements ContentView {

    private final MainView appLayout;

    public Component getContent() {
        try {
            Image image = getImageByResource("static/img/home_lib.png");
            VerticalLayout verticalLayout = new VerticalLayout(
                    new H3("LibHome - your book, game, music and other library."),
                    image,
                    getButton()
            );
            verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            return verticalLayout;

        } catch (IOException e) {
            e.printStackTrace();
            ViewUtils.showErrorMsg("ERROR: Load page.", e);
        }
        return null;
    }

    @SneakyThrows
    private Button getButton() {
        Button button = new Button("Card list");
        button.setHeightFull();
        button.addClickListener(btnClickEvent -> appLayout.setContent(appLayout.getCardView().getContent()));

        Image icon = ViewUtils.getImageByResource("static/img/icon/icon.png");
        icon.setHeight(32, Unit.PIXELS);
        icon.setWidth(32, Unit.PIXELS);
        button.setIcon(icon);

        return button;
    }
}
