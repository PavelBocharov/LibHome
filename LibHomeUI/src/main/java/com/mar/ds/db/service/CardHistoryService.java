package com.mar.ds.db.service;

import com.mar.ds.db.diff.DiffCard;
import com.mar.libhome.controller.CardHistoryRemote;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardHistoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static com.mar.ds.db.diff.DiffCard.CARD_RATE;
import static java.util.Objects.nonNull;

/**
 * Сервис по работе с изменениями карточки.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardHistoryService {

    private final CardHistoryRemote cardHistoryRemote;

    /**
     * Сохранить данные по новой карточки.
     *
     * @param card карточка.
     */
    public void saveCreateCard(CardDto card) {
        saveHistory(null, card);
    }

    /**
     * Определить изменения и сохранить в БД.
     *
     * @param old    старая версия карточки.
     * @param actual новая версия карточки.
     */
    public void saveHistory(CardDto old, CardDto actual) {
        assert nonNull(actual);
        log.debug("save/upd card history old: {}, actual: {}", old, actual);
        List<CardHistoryDto> diff = DiffCard.compare(old, actual);
        log.debug("save/upd card history: {}", diff);
        cardHistoryRemote.saveAll(diff);
    }

    /**
     * Определить изменения и сохранить в БД.
     *
     * @param cards список пар СТАРЫХ и НОВЫХ карточек.
     */
    public void saveHistory(List<Pair<CardDto, CardDto>> cards) {
        assert nonNull(cards);
        List<CardHistoryDto> diff = new LinkedList<>();

        for (Pair<CardDto, CardDto> oldNewCards : cards) {
            CardDto oldCard = oldNewCards.getLeft();
            CardDto newCard = oldNewCards.getRight();
            diff.addAll(DiffCard.compare(oldCard, newCard));
        }
        log.debug("save/upd card list history: {}", diff);
        cardHistoryRemote.saveAll(diff);
    }

    /**
     * Сохраняем данные об удалении карточки.
     *
     * @param card карточка.
     */
    public void saveDeleteCard(CardDto card) {
        cardHistoryRemote.save(
                CardHistoryDto.builder()
                        .editableId(card.getId())
                        .columnName("DELETED")
                        .titlePage(String.valueOf(card.getViewType()))
                        .oldValue(card.toString())
                        .newValue("")
                        .build()
        );
    }

    /**
     * Найти всю историю по ID.
     *
     * @param cardId ID карточки.
     * @return история изменения карточки.
     */
    public List<CardHistoryDto> findAllByCardId(UUID cardId) {
        return cardHistoryRemote.findAllByEditableId(cardId);
    }

    /**
     * Удалить всю историю по изменению рейтинга.
     *
     * @return удаленная история.
     */
    @Deprecated
    public List<CardHistoryDto> deleteAllByRate() {
        return cardHistoryRemote.deleteByColumnName(CARD_RATE);
    }

}
