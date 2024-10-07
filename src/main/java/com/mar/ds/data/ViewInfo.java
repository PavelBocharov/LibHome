package com.mar.ds.data;

import com.mar.ds.db.entity.ViewType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewInfo implements Serializable {

    private ViewType type;
    private List<GridInfo> gridInfoList;

}
