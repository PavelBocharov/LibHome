package com.mar.ds.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GridInfo implements Serializable {

    private String column;
    private String title;

    public static final String GRID_ID = "grid_id";
    public static final String GRID_STATUS = "grid_status";
    public static final String GRID_ENGINE = "grid_engine";
    public static final String GRID_TITLE = "grid_title";
    public static final String GRID_POINT = "grid_point";
    public static final String GRID_RATE = "grid_rate";
    public static final String GRID_DATE_UPD = "grid_date_upd";
    public static final String GRID_DATE_GAME = "grid_date_game";
    public static final String GRID_TYPE = "grid_type";
    public static final String GRID_TAGS = "grid_tags";
    public static final String GRID_LINK = "grid_link";
    public static final String GRID_BTNS = "grid_btns";

}
