package com.mar.ds.db.remote;

import com.mar.libhome.api.CardTypeApi;
import com.mar.libhome.dto.CardTypeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Profile("production")
@RequiredArgsConstructor
public class CardTypeRemoteImpl implements CardTypeRemote {

    public final CardTypeApi cardTypeApi;

    public List<CardTypeDto> findAll() {
        return cardTypeApi.getAllCardType();
    }

    public List<CardTypeDto> saveAll(List<CardTypeDto> dtoList) {
        return cardTypeApi.saveCardTypeList(dtoList);
    }

    public CardTypeDto remove(CardTypeDto dto) {
        return cardTypeApi.deleteCardType(dto);
    }

}
