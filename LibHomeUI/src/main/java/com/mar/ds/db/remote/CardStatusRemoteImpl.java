package com.mar.ds.db.remote;

import com.mar.libhome.api.CardStatusApi;
import com.mar.libhome.controller.CardStatusRemote;
import com.mar.libhome.dto.CardStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@Profile("production")
@RequiredArgsConstructor
public class CardStatusRemoteImpl implements CardStatusRemote {

    private final CardStatusApi cardStatusApi;

    public List<CardStatusDto> findAll() {
        return cardStatusApi.getAllCardStatus();
    }

    public List<CardStatusDto> saveAll(List<CardStatusDto> cardStatusList) {
        return cardStatusApi.saveCardStatusList(cardStatusList);
    }

    public CardStatusDto save(CardStatusDto diff) {
        return saveAll(Collections.singletonList(diff)).get(0);
    }

    public CardStatusDto remove(CardStatusDto dto) {
        return cardStatusApi.deleteCardStatus(dto);
    }

    public CardStatusDto findByTechId(String techId) {
        if (isBlank(techId)) {
            return null;
        }
        return cardStatusApi.findCardStatusByTechId(techId);
    }

    public List<CardStatusDto> findByTechIdIsNull() {
        return cardStatusApi.findAllCardStatusWithTechIsNull();
    }

}
