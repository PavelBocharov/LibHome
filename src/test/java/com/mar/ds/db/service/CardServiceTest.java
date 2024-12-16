package com.mar.ds.db.service;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.jpa.CardHistoryRepository;
import com.mar.ds.db.jpa.CardRepository;
import com.mar.ds.db.mapper.CardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.util.Date;

import static com.mar.ds.db.service.CardStatusService.TECH_HASE_UPD_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class CardServiceTest {

    static CardStatus hasUpd_tech = CardStatus.builder()
            .tech(TECH_HASE_UPD_ID)
            .hasUpdStatus(false)
            .build();

    CardRepository cardRepository = Mockito.mock(CardRepository.class);
    CardStatusService cardStatusService = Mockito.mock(CardStatusService.class);
    CardHistoryService cardHistoryRepository = Mockito.mock(CardHistoryService.class);
    CardService cardService = new CardService(cardRepository, cardStatusService, cardHistoryRepository, Mappers.getMapper(CardMapper.class));

    @BeforeEach
    void init() {
        when(cardStatusService.findByTechId(TECH_HASE_UPD_ID)).thenReturn(hasUpd_tech);
    }

    @Test
    void checkCard() {
        CardStatus status = CardStatus.builder()
                .hasUpdStatus(true)
                .isRate(true)
                .build();

        Card card = Card.builder()
                .point(9.5)
                .cardStatus(hasUpd_tech)
                .oldCardStatus(status)
                .build();

        Date now = new Date();
        Date before = new Date(now.getTime() - 10_000_000);
        Date after = new Date(now.getTime() + 10_000_000);
        // has upd - статусы не меняем
        card.setLastUpdate(after);
        card.setLastGame(now);

        card = cardService.checkCard(card);

        assertEquals(hasUpd_tech, card.getCardStatus());
        assertEquals(status, card.getOldCardStatus());
        // has upd - меняем статус на upd
        card.setLastUpdate(now);
        card.setLastGame(before);
        card.setCardStatus(status);
        card.setOldCardStatus(null);

        card = cardService.checkCard(card);

        assertEquals(hasUpd_tech, card.getCardStatus());
        assertEquals(status, card.getOldCardStatus());
        // return status
        card.setLastUpdate(before);
        card.setLastGame(now);
        card.setCardStatus(hasUpd_tech);
        card.setOldCardStatus(status);

        card = cardService.checkCard(card);

        assertEquals(status, card.getCardStatus());
        assertNull(card.getOldCardStatus());
    }
}