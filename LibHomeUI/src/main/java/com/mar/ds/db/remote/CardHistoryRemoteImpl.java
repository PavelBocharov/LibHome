package com.mar.ds.db.remote;

import com.mar.libhome.api.CardHistoryApi;
import com.mar.libhome.dto.CardHistoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Profile("production")
@RequiredArgsConstructor
public class CardHistoryRemoteImpl implements CardHistoryRemote {

    private final CardHistoryApi cardHistoryApi;

    @Override
    public List<CardHistoryDto> saveAll(List<CardHistoryDto> diff) {
        return cardHistoryApi.saveCardHistory(diff);
    }

    @Override
    public CardHistoryDto save(CardHistoryDto diff) {
        return saveAll(Collections.singletonList(diff)).get(0);
    }

    @Override
    public List<CardHistoryDto> findAllByEditableId(UUID editableId) {
        return cardHistoryApi.getCardHistoryByCardId(editableId);
    }

    @Override
    @Deprecated
    public List<CardHistoryDto> deleteByColumnName(String columnName) {
        return Collections.emptyList();
    }

}
