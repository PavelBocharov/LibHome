package com.mar.ds.db.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum GameEngine implements Serializable {

    DEFAULT("Default", "imgs/engine/engine_icon.png"),
    HTML("HTML", "imgs/engine/html_icon.png"),
    UNITY("Unity", "imgs/engine/unity_icon.png"),
    UE("Unreal Engine", "imgs/engine/unreal_engine_icon.png"),
    QSP("QSP", "imgs/engine/qsp_icon.jpg"),
    RPGM("RPG Maker", "imgs/engine/RPG_Maker_icon.png"),
    RENPY("RenPy", "imgs/engine/RenPy_icon.png"),
    DREAMCAST("Dreamcast", "imgs/engine/dreamcast-icon.png"),
    SWITCH("Nintendo Switch", "imgs/engine/nintendo-switch-icon.png"),
    PS4("PlayStation 4", "imgs/engine/ps4-icon.png"),
    PS5("PlayStation 5", "imgs/engine/ps5-icon.png"),
    XBOX("Xbox One", "imgs/engine/xbox-icon.png")
    ;

    private String name;
    private String iconPath;

}
