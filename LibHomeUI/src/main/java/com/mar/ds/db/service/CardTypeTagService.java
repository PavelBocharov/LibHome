package com.mar.ds.db.service;

import com.mar.ds.db.remote.CardTypeTagRemote;
import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.dto.CardTypeTagDto;
import com.mar.libhome.view.ViewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class CardTypeTagService implements ViewRepository<CardTypeTagDto> {

    @Autowired
    private CardTypeTagRemote remote;

    public List<CardTypeTagDto> findByCardType(CardTypeDto dto) {
        return remote.findByCardType(dto);
    }

    public CardTypeTagDto deleteById(UUID id) {
        return remote.deleteById(id);
    }

    @Override
    public List<CardTypeTagDto> findAll() {
        return remote.findAll();
    }

    @Override
    public CardTypeTagDto save(CardTypeTagDto dto) {
        return remote.save(Collections.singletonList(dto)).get(0);
    }

    @Override
    public CardTypeTagDto delete(CardTypeTagDto dto) {
        assert nonNull(dto);
        return remote.remove(dto);
    }
}
