package com.mar.ds.db.remote;

import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardStatusDto;
import com.mar.libhome.dto.CardTypeDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class CardRemote {

    public List<CardDto> findAll() {
        return Collections.emptyList();
    }

    public List<CardDto> findAllByView(Integer view, PageRequest pageRequest) {
        return Collections.emptyList();
    }

    public List<CardDto> findAllByViewAndLikeTitleMap(Integer view, String searchText, PageRequest pageRequest) {
//        PageRequest pageRequest = PageRequest.of(
//                1, 2, Sort.by(
//                        Sort.Order.asc("prop1"),
//                        Sort.Order.desc("prop2")
//                )
//        );

        return Collections.emptyList();
    }

    public List<CardDto> findWithOrderByPoint(Integer viewType) {
        return Collections.emptyList();
    }

    public List<CardDto> findByCardStatus(CardStatusDto cardStatus) {
        return Collections.emptyList();
    }

    public List<CardDto> findByTagId(UUID tagId) {
        return Collections.emptyList();
    }

    public List<CardDto> findByCardType(CardTypeDto cardType) {
        return Collections.emptyList();
    }

    public List<CardDto> saveAll(List<CardDto> dtoList) {
        return Collections.emptyList();
    }

    public CardDto save(CardDto dto) {
        return dto;
    }

    public CardDto delete(CardDto dto) {
        return dto;
    }

}
