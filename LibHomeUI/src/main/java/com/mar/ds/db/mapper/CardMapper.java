package com.mar.ds.db.mapper;

import com.mar.ds.db.dto.CardDto;
import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.libhome.dto.CardStatusDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.UUID;

/**
 * Маппер карточки.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {

    CardDto toDto(Card card);

    Card toEntity(CardDto card);

    default CardStatus status(CardStatusDto dto) {
        return CardStatus.builder()
                .id(dto.getId().getLeastSignificantBits())
                .color(dto.getColor())
                .tech(dto.getTech())
                .hasUpdStatus(dto.getHasUpdStatus())
                .icon(dto.getIcon())
                .title(dto.getTitle())
                .isRate(dto.getIsRate())
                .order(dto.getOrder())
                .build();
    }

    default CardStatusDto status(CardStatus cardStatus) {
        return CardStatusDto.builder()
                .id(new UUID(cardStatus.getId(), cardStatus.getId()))
                .color(cardStatus.getColor())
                .tech(cardStatus.getTech())
                .hasUpdStatus(cardStatus.getHasUpdStatus())
                .icon(cardStatus.getIcon())
                .title(cardStatus.getTitle())
                .isRate(cardStatus.getIsRate())
                .order(cardStatus.getOrder())
                .build();
    }

}
