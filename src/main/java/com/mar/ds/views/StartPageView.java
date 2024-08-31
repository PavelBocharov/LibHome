package com.mar.ds.views;

import com.mar.ds.utils.ViewUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import static com.mar.ds.utils.ViewUtils.getImageByResource;

@RequiredArgsConstructor
public class StartPageView implements ContentView {

    private final MainView appLayout;

    public Component getContent() {
        try {
            Image image = getImageByResource("static/img/home_lib.png");
            image.setWidthFull();
            image.setMaxWidth(360.0f, Unit.PIXELS);


            VerticalLayout verticalLayout = new VerticalLayout(
                    new H3("LibHome - your book, game, music and other library."),
                    image,
                    getButton(
                            "Card list",
                            VaadinIcon.BULLETS,
                            btnClickEvent -> appLayout.setContent(appLayout.getCardView().getContent())
                    )
            );
            verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            return verticalLayout;

        } catch (IOException e) {
            ViewUtils.showErrorMsg("ERROR: Load page.", e);
        }
        return null;
    }

    private Button getButton(String title, VaadinIcon icon, ComponentEventListener<ClickEvent<Button>> listener) {
        Button button = new Button(title, new Icon(icon));
        button.setHeightFull();
        button.addClickListener(listener);
//        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }
}
