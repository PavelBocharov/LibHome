package com.mar.libhome.db.mongo.mapper;

import com.mar.libhome.db.mongo.entity.CardHistory;
import com.mar.libhome.dto.CardHistoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardHistoryMapper {

    CardHistory toEntity(CardHistoryDto dto);

    CardHistoryDto toDto(CardHistory entity);

}
