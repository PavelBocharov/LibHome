package com.mar.ds.db.entity;

import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ViewType {

    NULL(1, "Start page", VaadinIcon.HOME),
    GAME(2, "Games", VaadinIcon.GAMEPAD),
    VIDEO(3, "Films & Movies", VaadinIcon.FILM),
    BOOK(4, "Book, Comix & Artbook", VaadinIcon.BOOK),
    MUSIC(5, "Music", VaadinIcon.MUSIC);

    private final Integer id;
    private final String title;
    private final VaadinIcon icon;

}
