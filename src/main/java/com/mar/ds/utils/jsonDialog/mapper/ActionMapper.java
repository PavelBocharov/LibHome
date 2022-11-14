package com.mar.ds.utils.jsonDialog.mapper;

import com.mar.ds.db.entity.Action;
import com.mar.ds.utils.jsonDialog.jsonData.ActionData;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActionMapper {

//    @Mapping(target = "status", source = "item.status.enumNumber")
//    @Mapping(target = "type", source = "item.type.enumNumber")
    ActionData getItemData(Action action);


//    List<ItemData> getItemDataList(List<Item> itemList);

}
