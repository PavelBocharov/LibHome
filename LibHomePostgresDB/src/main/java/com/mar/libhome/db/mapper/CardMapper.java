package com.mar.libhome.db.mapper;

import com.mar.libhome.db.entity.Card;
import com.mar.libhome.dto.CardDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                CardTypeMapper.class, CardStatusMapper.class, CardTypeTagMapper.class
        }
)
public interface CardMapper {

    Card toEntity(CardDto dto);

    CardDto toDto(Card entity);


}
