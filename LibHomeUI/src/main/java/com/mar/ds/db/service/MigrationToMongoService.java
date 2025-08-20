package com.mar.ds.db.service;

import com.mar.ds.db.entity.CardHistory;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.jpa.CardHistoryRepository;
import com.mar.ds.db.jpa.CardStatusRepository;
import com.mar.ds.db.remote.CardHistoryRemote;
import com.mar.ds.db.remote.CardStatusRemote;
import com.mar.libhome.dto.CardHistoryDto;
import com.mar.libhome.dto.CardStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationToMongoService {

    public static final int MAX_ARRAY_SEND_SIZE = 50;

    private final CardHistoryRepository cardHistoryRepository;
    private final CardStatusRepository cardStatusRepository;
    private final CardHistoryRemote cardHistoryRemote;
    private final CardStatusRemote cardStatusRemote;

    public void moveHistory() {
        List<CardHistory> cardHistoryList = cardHistoryRepository.findAll();
        int j = 0;
        List<CardHistoryDto> dtos = new ArrayList<>(MAX_ARRAY_SEND_SIZE);
        for (CardHistory c : cardHistoryList) {
            if (j == MAX_ARRAY_SEND_SIZE) {
                cardHistoryRemote.saveAll(dtos);
                j = 0;
                dtos.clear();
            }
            dtos.add(CardHistoryDto.builder()
                    .id(new UUID(c.getId(), c.getId()))
                    .oldValue(c.getOldValue())
                    .newValue(c.getNewValue())
                    .editableId(new UUID(c.getEditableId(), c.getEditableId()))
                    .titlePage(c.getTitlePage())
                    .updateCardTime(c.getUpdateCardTime())
                    .columnName(c.getColumnName())
                    .build()
            );
            j++;
        }
        if (!dtos.isEmpty()) {
            cardHistoryRemote.saveAll(dtos);
        }
    }

    public void moveStatus() {
        List<CardStatus> cardStatusList = cardStatusRepository.findAll();
        int j = 0;
        List<CardStatusDto> dtos = new ArrayList<>(MAX_ARRAY_SEND_SIZE);
        for (CardStatus cardStatus : cardStatusList) {
            if (j == MAX_ARRAY_SEND_SIZE) {
                cardStatusRemote.saveAll(dtos);
                j = 0;
                dtos.clear();
            }
            dtos.add(CardStatusDto.builder()
                    .id(new UUID(cardStatus.getId(), cardStatus.getId()))
                    .color(cardStatus.getColor())
                    .tech(cardStatus.getTech())
                    .hasUpdStatus(cardStatus.getHasUpdStatus())
                    .icon(cardStatus.getIcon())
                    .title(cardStatus.getTitle())
                    .isRate(cardStatus.getIsRate())
                    .order(cardStatus.getOrder())
                    .build()
            );
            j++;
        }
        if (!dtos.isEmpty()) {
            cardStatusRemote.saveAll(dtos);
        }
    }

}
