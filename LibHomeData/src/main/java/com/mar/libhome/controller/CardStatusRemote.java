package com.mar.libhome.controller;

import com.mar.libhome.dto.CardStatusDto;

import java.util.List;

/**
 * Репозиторий работы с таблицей изменений карточки.
 */
public interface CardStatusRemote {

    List<CardStatusDto> findAll();

    List<CardStatusDto> saveAll(List<CardStatusDto> cardStatusList);

    CardStatusDto save(CardStatusDto diff);

    CardStatusDto remove(CardStatusDto dto);

    CardStatusDto findByTechId(String techId);

    List<CardStatusDto> findByTechIdIsNull();

}
