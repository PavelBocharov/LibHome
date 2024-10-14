package com.mar.ds.views;

import com.mar.ds.db.entity.ViewType;
import com.mar.ds.utils.ViewUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import static com.mar.ds.utils.ViewUtils.getImageByResource;

@RequiredArgsConstructor
public class StartPageView implements ContentView {

    private final MainView appLayout;
    @Getter
    private final ViewType viewType;

    public Component getContent() {
        try {
            Image image = new Image("imgs/home_lib.png", "Alt text");
            VerticalLayout verticalLayout = new VerticalLayout(
                    new H3("LibHome - your book, game, music and other library."),
                    image
            );
            verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            return verticalLayout;
        } catch (Exception e) {
            e.printStackTrace();
            ViewUtils.showErrorMsg("ERROR: Load page.", e);
        }
        return null;
    }
}
