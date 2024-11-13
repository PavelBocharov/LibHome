package com.mar.ds.utils;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ButtonBuilder {

    private Button button;

    public static ButtonBuilder createButton() {
        ButtonBuilder bb = new ButtonBuilder();
        bb.button = new Button();
        return bb;
    }

    public Button build() {
        return this.button;
    }

    public ButtonBuilder text(String text) {
        this.button.setText(text);
        return this;
    }

    public ButtonBuilder icon(VaadinIcon icon) {
        if (icon != null) {
            this.button.setIcon(new Icon(icon));
        }
        return this;
    }

    public ButtonBuilder color(Color color) {
        if (nonNull(color)) {
            return color(color.getName());
        }
        return this;
    }

    public ButtonBuilder color(String color) {
        if (nonNull(color)) {
            this.button.getStyle().set("color", color);
        }
        return this;
    }

    public ButtonBuilder clickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        this.button.addClickListener(listener);
        return this;
    }

    @Getter
    @AllArgsConstructor
    public enum Color {
        RED("red"),
        BLACK("black"),
        BLUE("blue"),
        GREEN("green");

        private String name;
    }
}