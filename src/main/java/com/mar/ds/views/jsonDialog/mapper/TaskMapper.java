package com.mar.ds.views.jsonDialog.mapper;

import com.mar.ds.db.entity.Task;
import com.mar.ds.views.jsonDialog.jsonData.TaskData;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    TaskData getTaskData(Task task);

}
