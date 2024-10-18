package com.mar.ds.db.entity;

import com.mar.ds.views.card.CardView;
import com.vaadin.flow.component.html.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Language implements Serializable {

    DEFAULT("¯\\_(ツ)_/¯", "icons/lang/default.png"),
    ENG("English", "icons/lang/uk.png"),
    RU("Русский", "icons/lang/ru.png");

    private String title;
    /**
     * Emoji
     */
    private String icon;

    public Image getImage() {
        Image langIcon = new Image(icon, title);
        langIcon.setWidth(CardView.DEFAULT_GRID_ICON_SIZE_VAR);
        langIcon.setHeight(CardView.DEFAULT_GRID_ICON_SIZE_VAR);
        langIcon.getStyle().set("margin-bottom", "-6px");
        return langIcon;
    }

    public Image getImage(int size) {
        Image langIcon = getImage();
        langIcon.setWidth("var(--iron-icon-width, " + size + "px)");
        langIcon.setHeight("var(--iron-icon-width, " + size + "px)");
        return langIcon;
    }

}
