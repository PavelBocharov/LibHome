package com.mar.ds.utils.jsonDialog.mapper;

import com.mar.ds.db.entity.Dialog;
import com.mar.ds.db.entity.Document;
import com.mar.ds.db.entity.Item;
import com.mar.ds.utils.jsonDialog.jsonData.DialogData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ActionMapper.class}
)
public interface DialogMapper {

    @Mapping(target = "characterId", source = "dialog.character.id")
    @Mapping(target = "itemsId", source = "dialog.items")
    @Mapping(target = "documentsId", source = "dialog.documents")
    DialogData getDialogData(Dialog dialog);

    List<DialogData> getDialogDataList(List<Dialog> itemList);

    default List<Long> getItems(List<Item> itemList) {
        if (itemList == null || itemList.isEmpty()) return Collections.emptyList();
        return itemList.stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());
    }

    default List<Long> getDocuments(List<Document> documents) {
        if (documents == null || documents.isEmpty()) return Collections.emptyList();
        return documents.stream()
                .map(document -> document.getId())
                .collect(Collectors.toList());
    }

}
