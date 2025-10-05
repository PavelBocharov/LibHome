package com.mar.libhome.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Language implements Serializable {

    DEFAULT("¯\\_(ツ)_/¯", "icons/lang/default.png"),
    ENG("English", "icons/lang/uk.png"),
    RU("Русский", "icons/lang/ru.png"),
    //    https://www.flaticon.com/packs/flags-10
//    CA("Català", "icons/lang/ca.png"),
    DE("Deutsch", "icons/lang/de.png"),
    ES("Español", "icons/lang/es.png"),
    FI("Suomi (Finland)", "icons/lang/fi.png"),
    FR("Français", "icons/lang/fr.png"),
    GA("Gaeilge (Irish)", "icons/lang/ga.png"),
    HI("हिंदी (India)", "icons/lang/hi.png"),
    ID("Bahasa Indonesia", "icons/lang/id.png"),
    IT("Italiano", "icons/lang/it.png"),
    JA("日本語 (Japanese)", "icons/lang/ja.png"),
    KO("한국어 (Korean)", "icons/lang/ko.png"),
    PT("Português", "icons/lang/pt.png"),
    TR("Türkçe", "icons/lang/tr.png"),
    UK("Українська", "icons/lang/ukr.png"),
    CH("简体中文 (Chinese)", "icons/lang/ch.png"),
    ;

    private String title;
    /**
     * Emoji
     */
    private String icon;

}
