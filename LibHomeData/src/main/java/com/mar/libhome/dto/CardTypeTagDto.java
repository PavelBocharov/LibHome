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
public class CardTypeTagDto implements HasId, Serializable, PopupEntity {

    private UUID id;
    private UUID cardTypeId;
    private String title;

    @Override
    public Long getLongId() {
        return id.getLeastSignificantBits();
    }
}
