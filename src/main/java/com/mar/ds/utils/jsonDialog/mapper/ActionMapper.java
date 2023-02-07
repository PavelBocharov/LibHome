package com.mar.ds.utils.jsonDialog.mapper;

import com.mar.ds.db.entity.Action;
import com.mar.ds.utils.jsonDialog.jsonData.ActionData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActionMapper {

    @Mapping(target = "title", source = "action.text")
    @Mapping(target = "next", source = "action.openedDialog.id")
    @Mapping(target = "needMissionId", source = "action.needMission.id")
    @Mapping(target = "needTaskId", source = "action.needTask.id")
    @Mapping(target = "needItemId", source = "action.needItem.id")
    @Mapping(target = "generateType", source = "action.generateType.enumNumber")
    ActionData getActionData(Action action);


    List<ActionData> getActionDataList(List<Action> itemList);

}
