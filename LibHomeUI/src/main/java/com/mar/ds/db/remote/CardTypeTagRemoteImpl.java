package com.mar.ds.db.remote;

import com.mar.libhome.api.CardTypeTagApi;
import com.mar.libhome.controller.CardTypeTagRemote;
import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.dto.CardTypeTagDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Profile("production")
@RequiredArgsConstructor
public class CardTypeTagRemoteImpl implements CardTypeTagRemote {

    private final CardTypeTagApi cardTypeTagApi;

    public List<CardTypeTagDto> findByCardType(CardTypeDto dto) {
        return cardTypeTagApi.findAllCardTypeTagByCardTypeId(dto.getId());
    }

    public List<CardTypeTagDto> findAll() {
        return cardTypeTagApi.getAllCardTypeTag();
    }

    public List<CardTypeTagDto> save(List<CardTypeTagDto> dtoList) {
        return cardTypeTagApi.saveCardTypeTagList(dtoList);
    }

    public CardTypeTagDto deleteById(UUID id) {
        return remove(CardTypeTagDto.builder().id(id).build());
    }

    public CardTypeTagDto remove(CardTypeTagDto dto) {
        return cardTypeTagApi.deleteCardTypeTag(dto);
    }

}
