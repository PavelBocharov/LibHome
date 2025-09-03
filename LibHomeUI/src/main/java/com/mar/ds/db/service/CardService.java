package com.mar.ds.db.service;

import com.mar.ds.db.remote.CardRemote;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardStatusDto;
import com.mar.libhome.dto.CardTypeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.constraints.NotNull;

import static com.mar.ds.db.service.CardStatusService.TECH_HASE_UPD_ID;
import static com.mar.ds.utils.Utils.getDateWithoutTime;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.nonNull;

/**
 * Сервис по работе с карточки.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRemote cardRemote;
    private final CardStatusService cardStatusService;
    private final CardHistoryService cardHistoryService;

    /**
     * Проверяет все карточки в БД и обновляет (рейтинг, статус и т.д.).
     */
    public void checkAndUpdateAllCards() {
        List<com.mar.libhome.dto.CardDto> cards = cardRemote.findAll();

        ArrayList<CardDto> forUpd = new ArrayList<>(cards.size());
        List<Pair<CardDto, CardDto>> oldNewCards = new LinkedList<>();
        for (CardDto oldDto : cards) {
            CardDto newDto = checkCard(oldDto);
            forUpd.add(newDto);
            oldNewCards.add(Pair.of(oldDto, newDto));
        }
        saveAll(forUpd);
        cardHistoryService.saveHistory(oldNewCards);
    }

    public Page<CardDto> findAllByView(@NotNull Integer view, PageRequest pageRequest) {
        List<CardDto> dtoList = cardRemote.findAllByView(view, pageRequest);
        return new PageImpl<>(dtoList);
    }

    public Page<CardDto> findAllByViewAndLikeTitleMap(@NotNull Integer view, String searchText, PageRequest pageRequest) {
        List<CardDto> dtoList = cardRemote.findAllByViewAndLikeTitleMap(view, searchText, pageRequest);
        return new PageImpl<>(dtoList);
    }

    /**
     * Проверка карточки на валидность заполнения данных.
     * Изменение статуса в зависимости от времени и флага технического статуса.
     *
     * @param card карточка.
     * @return проверенная карточка.
     */
    public CardDto checkCard(CardDto card) {
        assert nonNull(card);
        assert nonNull(card.getCardStatus());

        // update status
        CardStatusDto status = card.getCardStatus();
        CardStatusDto oldStatus = card.getOldCardStatus();

        if (nonNull(card.getLastGame()) && nonNull(card.getLastUpdate())
                && card.getLastUpdate().after(card.getLastGame())
        ) {
            if (!status.isTech() && TRUE.equals(status.getHasUpdStatus())) {
                CardStatusDto hasUpdStatus = cardStatusService.findByTechId(TECH_HASE_UPD_ID);
                card.setOldCardStatus(status);
                card.setCardStatus(
                        CardStatusDto.builder()
                                .id(hasUpdStatus.getId())
                                .order(hasUpdStatus.getOrder())
                                .hasUpdStatus(hasUpdStatus.getHasUpdStatus())
                                .isRate(hasUpdStatus.getIsRate())
                                .title(hasUpdStatus.getTitle())
                                .icon(hasUpdStatus.getIcon())
                                .color(hasUpdStatus.getColor())
                                .tech(hasUpdStatus.getTech())
                                .build()
                );
            }
        } else {
            if (status.isTech()) {
                card.setCardStatus(oldStatus);
            }
            card.setOldCardStatus(null);
        }

        if (card.getCardStatus().isTech() && !card.getOldCardStatus().getHasUpdStatus()) {
            card.setCardStatus(card.getOldCardStatus());
            card.setOldCardStatus(null);
        }
        if (!card.getCardStatus().isTech() && !card.getCardStatus().getHasUpdStatus()) {
            card.setOldCardStatus(null);
        }

        calcRate(card);
        return card;
    }

    public CardDto save(CardDto card) {
        return cardRemote.save(checkCard(card));
    }

    /**
     * Сохранить все карточки.
     *
     * @param cards список карточек.
     * @return сохраненные карточки (с ID).
     */
    public List<CardDto> saveAll(Collection<CardDto> cards) {
        return cardRemote.saveAll(cards.stream().map(this::checkCard).toList());
    }

    public List<CardDto> findAll() {
        return cardRemote.findAll();
    }

    public void remove(CardDto card) {
        cardRemote.delete(card);
    }

    public List<CardDto> findWithOrderByPoint(Integer viewType) {
        return cardRemote.findWithOrderByPoint(viewType);
    }

    public List<CardDto> findByCardStatus(CardStatusDto cardStatus) {
        return cardRemote.findByCardStatus(cardStatus);
    }

    public List<CardDto> findByTag(UUID tagId) {
        return cardRemote.findByTagId(tagId);
    }

    public List<CardDto> findByCardType(CardTypeDto cardType) {
        return cardRemote.findByCardType(cardType);
    }

    private void calcRate(CardDto card) {
        if (!Optional.ofNullable(card.getCardStatus().getIsRate()).orElse(false)) {
            card.setRate(0.0);
            return;
        }

        long deltaGame = 0;
        if (card.getLastGame() != null) {
            long now = getDateWithoutTime(new Date());
            long lastGameTime = getDateWithoutTime(card.getLastGame());
            deltaGame = (now - lastGameTime) / 43200000;
        }
        card.setRate(card.getPoint() * deltaGame * 0.01);
    }
}
