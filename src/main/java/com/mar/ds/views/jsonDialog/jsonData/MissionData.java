package com.mar.ds.views.jsonDialog.jsonData;

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
public class MissionData implements Serializable {

    private String title;
    private String text;
    private List<TaskData> tasks;

}
