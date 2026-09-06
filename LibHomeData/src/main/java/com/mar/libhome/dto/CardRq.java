package com.mar.libhome.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class CardRq implements Serializable {

    Integer view;
    String searchText;
    UUID cardStatusId;
    UUID cardTypeId;
    UUID cardTagId;

    Integer page;
    Integer size;
    Map<String, SortOrder> sort;

    public enum SortOrder {
        ASC, DESC
    }

}
