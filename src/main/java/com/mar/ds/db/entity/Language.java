package com.mar.ds.db.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Language implements Serializable {

    DEFAULT("¯\\_(ツ)_/¯", "\uD83C\uDFF4\u200D☠\uFE0F"),
    ENG("English", "\uD83C\uDDEC\uD83C\uDDE7"),
    RU("Русский", "\uD83C\uDDF7\uD83C\uDDFA");

    private String title;
    /**
     * Emoji
     */
    private String icon;

}
