package com.mar.libhome.controller;

import com.mar.libhome.controller.data.PageRequest;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardRs;
import com.mar.libhome.dto.CardStatusDto;
import com.mar.libhome.dto.CardTypeDto;

import java.util.List;
import java.util.UUID;

public interface CardRemote {

    List<CardDto> findAll();

    CardRs findAll(PageRequest pageRequest);

    List<CardDto> saveAll(List<CardDto> dtoList);

    CardDto save(CardDto dto);

    CardDto remove(CardDto dto);

    CardRs findAllByView(Integer view, PageRequest pageRequest);

    CardRs findAllByViewAndLikeTitleMap(Integer view, String searchText, PageRequest pageRequest);

    CardRs findWithOrderByPoint(Integer viewType);

    CardRs findByCardStatus(CardStatusDto cardStatus);

    CardRs findByCardType(CardTypeDto cardType);

    List<CardDto> findByTagId(UUID tagId);

}
