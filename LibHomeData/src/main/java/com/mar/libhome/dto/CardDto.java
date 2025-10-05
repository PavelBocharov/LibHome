package com.mar.libhome.dto;

import com.mar.libhome.enums.GameEngine;
import com.mar.libhome.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Карточка.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDto implements Serializable {

    private UUID id;
    private Integer viewType;

    private String title;
    private Double point;
    private String info;
    private String link;
    private Date lastGame;
    private Date lastUpdate;
    private GameEngine engine;
    private Language language;
    private Double rate;
    private CardTypeDto cardType;
    private CardStatusDto cardStatus;
    private CardStatusDto oldCardStatus;
    private List<CardTypeTagDto> tagList;

}
