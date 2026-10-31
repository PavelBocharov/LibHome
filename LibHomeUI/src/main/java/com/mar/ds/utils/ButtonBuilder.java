package com.mar.ds.utils;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.util.Objects.nonNull;

/**
 * Билдер для кнопок.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ButtonBuilder {

    private BuilderButtonInfo button;

    /**
     * Создает пустую кнопку.
     *
     * @return билдер.
     */
    public static ButtonBuilder createButton() {
        ButtonBuilder bb = new ButtonBuilder();
        bb.button = new BuilderButtonInfo();
        return bb;
    }

    public Button build() {
        Button btn = new Button();
        btn.setText(this.button.getText());
        btn.setIcon(this.button.getIcon());
        btn.getStyle().set("color", this.button.getColor());
        btn.addClickListener(this.button.getListener());
        return btn;
    }

    public ButtonBuilder text(String text) {
        this.button.setText(text);
        return this;
    }

    /**
     * Добавить иконку.
     *
     * @param icon иконка.
     * @return билдер.
     */
    public ButtonBuilder icon(VaadinIcon icon) {
        if (icon != null) {
            this.button.setIcon(new Icon(icon));
        }
        return this;
    }

    /**
     * Добавить цвет текста и иконки.
     *
     * @param color цвет.
     * @return билдер.
     */
    public ButtonBuilder color(Color color) {
        if (nonNull(color)) {
            return color(color.getName());
        }
        return this;
    }

    /**
     * Добавить цвет текста и иконки.
     *
     * @param color цвет (HEX или то что браузер поймет).
     * @return билдер.
     */
    public ButtonBuilder color(String color) {
        if (nonNull(color)) {
            this.button.setColor(color);
        }
        return this;
    }

    public ButtonBuilder clickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        this.button.setListener(listener);
        return this;
    }

    /**
     * Заготовленные цвета.
     */
    @Getter
    @AllArgsConstructor
    public enum Color {
        RED("red"),
        BLACK("black"),
        BLUE("blue"),
        GREEN("green");

        private String name;
    }

    @Data
    private static class BuilderButtonInfo {
        private ComponentEventListener<ClickEvent<Button>> listener;
        private String color;
        private Icon icon;
        private String text;
    }
}