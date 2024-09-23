package com.mar.ds.db.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum GameEngine implements Serializable, HasId {

    DEFAULT("Default", "static/img/icon/engine/engine_icon.png"),
    HTML("HTML", "static/img/icon/engine/html_icon.png"),
    UNITY("Unity", "static/img/icon/engine/unity_icon.png"),
    UE("Unreal Engine", "static/img/icon/engine/unreal_engine_icon.png"),
    QSP("QSP", "static/img/icon/engine/qsp_icon.jpg"),
    RPGM("RPG Maker", "static/img/icon/engine/RPG_Maker_icon.png"),
    RENPY("RenPy", "static/img/icon/engine/RenPy_icon.png"),
    DREAMCAST("Dreamcast", "static/img/icon/engine/dreamcast-icon.png"),
    SWITCH("Nintendo Switch", "static/img/icon/engine/nintendo-switch-icon.png"),
    PS4("PlayStation 4", "static/img/icon/engine/ps4-icon.png"),
    PS5("PlayStation 5", "static/img/icon/engine/ps5-icon.png"),
    XBOX("Xbox One", "static/img/icon/engine/xbox-icon.png")
    ;

    private String name;
    private String iconPath;

    @Override
    public Long getId() {
        if (name == null) {
            return 0L;
        }
        return (long) name.hashCode();
    }
}
