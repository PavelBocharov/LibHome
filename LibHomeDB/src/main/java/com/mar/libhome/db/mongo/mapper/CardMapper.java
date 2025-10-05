package com.mar.libhome.db.mongo.mapper;

import com.mar.libhome.db.mongo.entity.Card;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardStatusDto;
import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.dto.CardTypeTagDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {

    @Mapping(target = "cardTypeId", source = "cardType.id")
    @Mapping(target = "cardStatusId", source = "cardStatus.id")
    @Mapping(target = "oldCardStatusId", source = "oldCardStatus.id")
    @Mapping(target = "tagIdList", source = "tagList")
    Card toEntity(CardDto dto);

    @Mapping(target = "tagList", source = "tagIdList")
    @Mapping(target = "cardType", source = "cardTypeId")
    @Mapping(target = "cardStatus", source = "cardStatusId")
    @Mapping(target = "oldCardStatus", source = "oldCardStatusId")
    CardDto toDto(Card entity);

    default UUID getTagId(CardTypeTagDto tag) {
        return tag == null ? null : tag.getId();
    }

    default CardTypeTagDto getTag(UUID tagId) {
        if (tagId == null) {
            return null;
        }
        return CardTypeTagDto.builder().id(tagId).build();
    }

    default CardTypeDto getType(UUID typeId) {
        if (typeId == null) {
            return null;
        }
        return CardTypeDto.builder().id(typeId).build();
    }

    default CardStatusDto getStatus(UUID statusId) {
        if (statusId == null) {
            return null;
        }
        return CardStatusDto.builder().id(statusId).build();
    }

}
