package com.mar.libhome.controller;

import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.dto.CardTypeTagDto;

import java.util.List;
import java.util.UUID;

public interface CardTypeTagRemote {

    List<CardTypeTagDto> findByCardType(CardTypeDto dto);

    List<CardTypeTagDto> findAll();

    List<CardTypeTagDto> save(List<CardTypeTagDto> dtoList);

    CardTypeTagDto deleteById(UUID id);

    CardTypeTagDto remove(CardTypeTagDto dto);

}
