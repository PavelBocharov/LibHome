package com.mar.ds.db.service;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardHistory;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.CardType;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.db.jpa.CardHistoryRepository;
import com.mar.ds.db.jpa.CardRepository;
import com.mar.ds.db.jpa.CardStatusRepository;
import com.mar.ds.db.jpa.CardTypeRepository;
import com.mar.ds.db.jpa.CardTypeTagRepository;
import com.mar.ds.db.remote.CardHistoryRemote;
import com.mar.ds.db.remote.CardRemote;
import com.mar.ds.db.remote.CardStatusRemote;
import com.mar.ds.db.remote.CardTypeRemote;
import com.mar.ds.db.remote.CardTypeTagRemote;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardHistoryDto;
import com.mar.libhome.dto.CardStatusDto;
import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.dto.CardTypeTagDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationToMongoService {

    public static final int MAX_ARRAY_SEND_SIZE = 50;

    // old sqlite
    private final CardRepository cardRepository;
    private final CardHistoryRepository cardHistoryRepository;
    private final CardStatusRepository cardStatusRepository;
    private final CardTypeRepository cardTypeRepository;
    private final CardTypeTagRepository cardTypeTagRepository;

    // remote mongo
    private final CardRemote cardRemote;
    private final CardHistoryRemote cardHistoryRemote;
    private final CardStatusRemote cardStatusRemote;
    private final CardTypeRemote cardTypeRemote;
    private final CardTypeTagRemote cardTypeTagRemote;

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

    public void moveTypesAndTags() {
        List<CardType> cardTypes = cardTypeRepository.findAll();
        if (cardTypes != null) {
            for (CardType cardType : cardTypes) {
                List<CardTypeTag> cardTypeTags = cardTypeTagRepository.findByCardType(cardType);

                CardTypeDto cardTypeDto = cardTypeRemote.saveAll(Collections.singletonList(
                                CardTypeDto.builder()
                                        .id(new UUID(cardType.getId(), cardType.getId()))
                                        .title(cardType.getTitle())
                                        .build()
                        )
                ).get(0);

                cardTypeTagRemote.save(
                        cardTypeTags.parallelStream().map(tag ->
                                CardTypeTagDto.builder()
                                        .id(new UUID(tag.getId(), tag.getId()))
                                        .cardTypeId(cardTypeDto.getId())
                                        .title(tag.getTitle())
                                        .build()
                        ).toList()
                );
            }
        }
    }

    public void moveCardsToMongoDB() {
        List<Card> cards = cardRepository.findAll();
        log.debug("Size cards to migrate: {}", cards.size());
        int i = 0;
        ArrayList<CardDto> cardsToSave = new ArrayList<>(25);
        for (Card card : cards) {
            if (i >= 25) {
                cardRemote.saveAll(cardsToSave);
                cardsToSave = new ArrayList<>(25);
                i = 0;
            }
            cardsToSave.add(
                    CardDto.builder()
                            .id(new UUID(card.getId(), card.getId()))
                            .viewType(card.getViewType())
                            .title(card.getTitle())
                            .point(card.getPoint())
                            .info(card.getInfo())
                            .link(card.getLink())
                            .lastUpdate(card.getLastUpdate())
                            .lastGame(card.getLastGame())
                            .engine(card.getEngine())
                            .language(card.getLanguage())
                            .rate(card.getRate())
                            .cardType(CardTypeDto.builder()
                                    .id(new UUID(card.getCardType().getId(), card.getCardType().getId()))
                                    .build()
                            )
                            .cardStatus(CardStatusDto.builder()
                                    .id(new UUID(card.getCardStatus().getId(), card.getCardStatus().getId()))
                                    .build()
                            )
                            .oldCardStatus(
                                    card.getOldCardStatus() == null
                                            ? null
                                            : CardStatusDto.builder()
                                            .id(new UUID(card.getOldCardStatus().getId(), card.getOldCardStatus().getId()))
                                            .build()
                            )
                            .tagList(card.getTagList().parallelStream()
                                    .map(tag -> CardTypeTagDto.builder().id(new UUID(tag.getId(), tag.getId())).build())
                                    .toList()
                            )
                            .build()
            );
            i++;
        }
        if (!cardsToSave.isEmpty()) {
            cardRemote.saveAll(cardsToSave);
        }
    }

}
