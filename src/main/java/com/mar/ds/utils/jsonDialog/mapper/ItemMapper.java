package com.mar.ds.utils.jsonDialog.mapper;

import com.mar.ds.db.entity.Item;
import com.mar.ds.utils.jsonDialog.jsonData.ItemData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    @Mapping(target = "status", source = "item.status.enumNumber")
    @Mapping(target = "type", source = "item.type.enumNumber")
    ItemData getItemData(Item item);
    List<ItemData> getItemDataList(List<Item> itemList);

}
