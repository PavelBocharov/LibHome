package com.mar.ds.db.service;

import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.dto.CardTypeTagDto;
import com.mar.libhome.view.ViewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class CardTypeTagService implements ViewRepository<CardTypeTagDto> {

    public List<CardTypeTagDto> findByCardType(CardTypeDto dto) {
        return Collections.emptyList();
    }

    public CardTypeTagDto deleteById(UUID id) {
        return new CardTypeTagDto();
    }

    @Override
    public List<CardTypeTagDto> findAll() {
        return List.of();
    }

    @Override
    public CardTypeTagDto save(CardTypeTagDto dto) {
        return null;
    }

    @Override
    public CardTypeTagDto delete(CardTypeTagDto dto) {
        assert nonNull(dto);

        return deleteById(dto.getId());
    }
}
