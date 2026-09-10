package com.mar.ds.db.remote;

import com.mar.libhome.dto.CardTypeDto;

import java.util.List;

public interface CardTypeRemote {

    List<CardTypeDto> findAll();

    List<CardTypeDto> saveAll(List<CardTypeDto> dtoList);

    CardTypeDto remove(CardTypeDto dto);

}
