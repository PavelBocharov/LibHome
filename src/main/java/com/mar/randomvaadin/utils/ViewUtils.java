package com.mar.randomvaadin.utils;

import com.google.common.io.Resources;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import static com.vaadin.flow.component.icon.VaadinIcon.CLOSE_SMALL;

@UtilityClass
public class ViewUtils {

    public static final int DEFAULT_DURATION_ERROR_MSG = 15_000;

    public static void showErrorMsg(String title, Throwable ex) {
        showErrorMsg(title, ex, DEFAULT_DURATION_ERROR_MSG);
    }

    public static void showErrorMsg(String title, Throwable ex, int duration) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setDuration(duration);
        notification.setPosition(Notification.Position.TOP_END);

        VerticalLayout layout = new VerticalLayout();
        Accordion accordion = new Accordion();
        accordion.add(title, new Label(ExceptionUtils.getRootCauseMessage(ex)));
        accordion.close();

        Button clsBtn = new Button("Закрыть");
        clsBtn.addClickListener(btnClick -> notification.close());

        layout.add(accordion, clsBtn);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, accordion, clsBtn);
        layout.getStyle().set("padding", "0px");

        notification.add(layout);
        notification.open();
    }

    public static Image getImageByResource(String pathInResource) throws IOException {
        URL imgUrl = Resources.getResource(pathInResource);
        byte[] img = Resources.asByteSource(imgUrl).read();
        return new Image(
                new StreamResource(
                        FileUtils.getFile(imgUrl.getFile()).getName(),
                        () -> new ByteArrayInputStream(img)), String.format("Not load image: %s", pathInResource)
        );
    }

    public static TextField getTextField(String text, boolean enable) {
        TextField textField = new TextField();
        textField.setReadOnly(true);
        textField.setValue(text);
        return textField;
    }

    public static Button getCloseButton(Dialog closeDialog) {
        Button clsBtn = new Button(new Icon(CLOSE_SMALL));
        clsBtn.getStyle().set("color", "red");
        clsBtn.addClickListener(btnClick -> closeDialog.close());
        return clsBtn;
    }

}
