package com.mar.ds.db.entity;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Language implements Serializable {

    DEFAULT("¯\\_(ツ)_/¯", "icons/lang/default.png"),
    ENG("English", "icons/lang/eng.png"),
    RU("Русский", "icons/lang/ru.png");

    private String title;
    /**
     * Emoji
     */
    private String icon;

    public Image getImage() {
        Image langIcon = new Image(icon, title);
        langIcon.setWidth(32, Unit.PIXELS);
        langIcon.setHeight(32, Unit.PIXELS);
        return langIcon;
    }

    public Image getImage(int size) {
        Image langIcon = getImage();
        langIcon.setWidth(size, Unit.PIXELS);
        langIcon.setHeight(size, Unit.PIXELS);
        return langIcon;
    }

}
