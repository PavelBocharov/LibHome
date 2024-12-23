package com.mar.ds.db.mapper;

import com.mar.ds.db.dto.CardDto;
import com.mar.ds.db.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {

    CardDto toDto(Card card);

    Card toEntity(CardDto card);

}
