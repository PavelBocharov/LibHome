package com.mar.ds.utils.jsonDialog.jsonData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentData implements Serializable {

    private Long id;
    private String btnTitle;
    private String title;
    private String text;
    private String image;
    private Long documentStatus;

}
