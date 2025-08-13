package com.mar.libhome.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Карточка.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto implements Serializable {

    private Long id;
    private Integer viewType;

//    private String title;
//    private Double point;
//    private String info;
//    private String link;
//    private Date lastGame;
//    private Date lastUpdate;
//    private GameEngine engine;
//    private Language language;
//    private Double rate;
//    private CardTypeDto cardType;
//    private CardStatusDto cardStatus;
//    private CardStatusDto oldCardStatus;
//    private List<CardTypeTagDto> tagList;

}
