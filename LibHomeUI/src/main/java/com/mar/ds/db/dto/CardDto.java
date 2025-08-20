package com.mar.ds.db.dto;

import com.mar.ds.db.entity.GameEngine;
import com.mar.ds.db.entity.Language;
import com.mar.libhome.dto.CardStatusDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Карточка.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto implements Serializable {

    private Long id;
    private String title;
    private Double point;
    private String info;
    private String link;
    private Date lastGame;
    private Date lastUpdate;
    private GameEngine engine;
    private Language language;
    private Integer viewType;
    private Double rate;
    private CardTypeDto cardType;
    private CardStatusDto cardStatus;
    private CardStatusDto oldCardStatus;
    private List<CardTypeTagDto> tagList;

}
