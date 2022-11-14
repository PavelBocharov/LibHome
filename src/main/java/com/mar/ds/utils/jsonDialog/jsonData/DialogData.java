package com.mar.ds.utils.jsonDialog.jsonData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DialogData implements Serializable {

    private Long id;
    private Long characterId;
    private String text;
    private List<ActionData> actions;
    private List<Long> itemsId;

}
