package com.mar.libhome.db.mapper;

import com.mar.libhome.db.entity.CardTypeTag;
import com.mar.libhome.dto.CardTypeTagDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardTypeTagMapper {

    CardTypeTag toEntity(CardTypeTagDto dto);

    CardTypeTagDto toDto(CardTypeTag entity);

}
