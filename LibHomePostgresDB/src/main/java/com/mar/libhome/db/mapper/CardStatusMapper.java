package com.mar.libhome.db.mapper;

import com.mar.libhome.db.entity.CardStatus;
import com.mar.libhome.dto.CardStatusDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardStatusMapper {

    CardStatus toEntity(CardStatusDto dto);

    CardStatusDto toDto(CardStatus entity);

}
