package com.mar.ds.db.service;

import com.mar.ds.db.entity.CardHistory;
import com.mar.ds.db.jpa.CardHistoryRepository;
import com.mar.ds.db.remote.CardHistoryRemote;
import com.mar.libhome.dto.CardHistoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MigrationToMongoService {

    public static final int MAX_ARRAY_SEND_SIZE = 50;

    @Autowired
    private CardHistoryRepository cardHistoryRepository;

    @Autowired
    private CardHistoryRemote cardHistoryRemote;

    public void moveHistory() {
        while (true) {
            try {
                Thread.sleep(5_000L);
                cardHistoryRemote.checkHealth();
                break;
            } catch (Exception e) {
                log.warn("BD not startup - {}", e.getMessage());
            }
        }

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

}
