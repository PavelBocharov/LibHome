package com.mar.libhome.dto;

import com.mar.libhome.view.PopupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class CardTypeDto implements Serializable, PopupEntity, HasId {

    private UUID id;
    private String title;

    @Override
    public Long getLongId() {
        if (id == null) {
            return null;
        }
        return id.getLeastSignificantBits();
    }
}
