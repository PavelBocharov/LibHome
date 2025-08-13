package com.mar.libhome.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Статус карточки (DTO).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardStatusDto implements Serializable {

    private UUID id;
    private String title;
    private String color;
    private String icon = "BULLSEYE";
    private Boolean isRate = true;
    private Boolean hasUpdStatus = false;
    private String tech;
    private Long order = 0L;

}
