package com.mar.libhome.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Статус карточки (DTO).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardStatusDto implements Serializable, HasId {

    private UUID id;
    private String title;
    private String color = "#177AD1";
    private String icon = "BULLSEYE";
    private Boolean isRate = true;
    private Boolean hasUpdStatus = false;
    private String tech;
    private Long order = 0L;

    public Long getLongId() {
        if (id == null) {
            return null;
        }
        return id.getLeastSignificantBits();
    }

    public boolean isTech() {
        return tech != null && !tech.isBlank();
    }

}
