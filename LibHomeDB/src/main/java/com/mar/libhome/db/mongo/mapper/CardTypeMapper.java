package com.mar.libhome.db.mongo.mapper;

import com.mar.libhome.db.mongo.entity.CardType;
import com.mar.libhome.dto.CardTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardTypeMapper {

    CardType toEntity(CardTypeDto dto);

    CardTypeDto toDto(CardType entity);

}
