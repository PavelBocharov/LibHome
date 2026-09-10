package com.mar.ds.db.remote.local;

import com.mar.ds.db.remote.CardHistoryRemote;
import com.mar.libhome.dto.CardHistoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Profile("!production")
public class CardHistoryRemoteImpl implements CardHistoryRemote {

    private static final List<CardHistoryDto> data = List.of(CardHistoryDto.builder().id(UUID.randomUUID()).build());

    @Override
    public List<CardHistoryDto> saveAll(List<CardHistoryDto> diff) {
        log.debug(">> Save all history to LOCAL.");
        return data;
    }

    @Override
    public CardHistoryDto save(CardHistoryDto diff) {
        return data.get(0);
    }

    @Override
    public List<CardHistoryDto> findAllByEditableId(UUID editableId) {
        log.debug(">> GET history by card id. LOCAL. UUID: {}", editableId);
        List<CardHistoryDto> rs = data;
        log.debug("<< GET history by card id. History mapping rs: {}", rs);
        return rs;
    }

    @Override
    @Deprecated
    public List<CardHistoryDto> deleteByColumnName(String columnName) {
        return Collections.emptyList();
    }

}
