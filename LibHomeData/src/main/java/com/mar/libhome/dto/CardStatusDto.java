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
    @Builder.Default private String color = "#177AD1";
    @Builder.Default private String icon = "BULLSEYE";
    @Builder.Default private Boolean isRate = true;
    @Builder.Default private Boolean hasUpdStatus = false;
    private String tech;
    @Builder.Default private Long order = 0L;

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
