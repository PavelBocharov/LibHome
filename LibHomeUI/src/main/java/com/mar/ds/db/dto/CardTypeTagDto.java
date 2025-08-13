package com.mar.ds.db.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardTypeTagDto implements Serializable {

    private Long id;
    private String title;

}
