package com.mar.libhome.db.mongo.mapper;

import com.mar.libhome.db.mongo.entity.CardStatus;
import com.mar.libhome.dto.CardStatusDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardStatusMapper {

    CardStatus toEntity(CardStatusDto dto);

    CardStatusDto toDto(CardStatus entity);

}
