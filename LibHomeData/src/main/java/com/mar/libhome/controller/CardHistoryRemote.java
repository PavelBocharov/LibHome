package com.mar.libhome.controller;

import com.mar.libhome.dto.CardHistoryDto;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий работы с таблицей изменений карточки.
 */
public interface CardHistoryRemote {

    List<CardHistoryDto> saveAll(List<CardHistoryDto> diff);

    CardHistoryDto save(CardHistoryDto diff);

    List<CardHistoryDto> findAllByEditableId(UUID editableId);

    @Deprecated
    List<CardHistoryDto> deleteByColumnName(String columnName);

}
