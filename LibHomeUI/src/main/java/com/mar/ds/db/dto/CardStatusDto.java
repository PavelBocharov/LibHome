package com.mar.ds.db.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Статус карточки (DTO).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardStatusDto implements Serializable {

    private Long id;
    private String title;
    private String color;
    private String icon;
    private Boolean isRate;
    private Boolean hasUpdStatus;
    private String tech;
    private Long order;

}
