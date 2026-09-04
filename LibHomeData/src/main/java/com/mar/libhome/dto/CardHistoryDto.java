package com.mar.libhome.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CardHistoryDto implements Serializable {

    private UUID id;
    private UUID editableId;
    @Builder.Default private Date updateCardTime = new Date();
    private String titlePage;
    private String columnName;
    private String oldValue;
    private String newValue;

}
