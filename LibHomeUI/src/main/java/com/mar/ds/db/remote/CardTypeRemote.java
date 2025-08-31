package com.mar.ds.db.remote;

import com.mar.libhome.dto.CardTypeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CardTypeRemote {

    public List<CardTypeDto> findAll() {
        return List.of();
    }

    public List<CardTypeDto> saveAll(List<CardTypeDto> dtoList) {
        return null;
    }

    public CardTypeDto delete(CardTypeDto dto) {
        return null;
    }

}
