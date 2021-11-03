package com.mar.randomvaadin.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.io.IOException;

import static com.mar.randomvaadin.utils.ViewUtils.getImageByResource;

public class StartPageView extends VerticalLayout {

    public StartPageView() {}

    public Component getContent() throws IOException {
        Image image = getImageByResource("static/img/vmu-01.png");
        image.setWidthFull();
        image.setMaxWidth(600.0f, Unit.PIXELS);

        VerticalLayout verticalLayout = new VerticalLayout(
                new H3("Тут всякий хлам который должен упростить жЫзнЪ"),
                image
        );
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        return verticalLayout;
    }

}
