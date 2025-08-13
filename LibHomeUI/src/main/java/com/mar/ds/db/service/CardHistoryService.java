package com.mar.ds.db.service;

import com.mar.ds.db.diff.DiffCard;
import com.mar.ds.db.dto.CardDto;
import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardHistory;
import com.mar.ds.db.jpa.CardHistoryRepository;
import com.mar.ds.db.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import javax.transaction.Transactional;

import static com.mar.ds.db.diff.DiffCard.CARD_RATE;
import static java.util.Objects.nonNull;

/**
 * Сервис по работе с изменениями карточки.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CardHistoryService {

    private final CardHistoryRepository cardHistoryRepository;
    private final CardMapper cardMapper;

    /**
     * Сохранить данные по новой карточки.
     *
     * @param card карточка.
     */
    public void saveCreateCard(Card card) {
        saveHistory(null, cardMapper.toDto(card));
    }

    /**
     * Определить изменения и сохранить в БД.
     *
     * @param old    старая версия карточки.
     * @param actual новая версия карточки.
     */
    public void saveHistory(@Nullable CardDto old, CardDto actual) {
        assert nonNull(actual);
        List<CardHistory> diff = DiffCard.compare(old, actual);
        log.debug("save/upd card history: {}", diff);
        cardHistoryRepository.saveAll(diff);
    }

    /**
     * Определить изменения и сохранить в БД.
     *
     * @param cards список пар СТАРЫХ и НОВЫХ карточек.
     */
    public void saveHistory(List<Pair<CardDto, CardDto>> cards) {
        assert nonNull(cards);
        List<CardHistory> diff = new LinkedList<>();

        for (Pair<CardDto, CardDto> oldNewCards : cards) {
            CardDto oldCard = oldNewCards.getLeft();
            CardDto newCard = oldNewCards.getRight();
            diff.addAll(DiffCard.compare(oldCard, newCard));
        }
        log.debug("save/upd card history: {}", diff);
        cardHistoryRepository.saveAll(diff);
    }

    /**
     * Сохраняем данные об удалении карточки.
     *
     * @param card карточка.
     */
    public void saveDeleteCard(CardDto card) {
        cardHistoryRepository.save(
                CardHistory.builder()
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
    public List<CardHistory> findAllByCardId(long cardId) {
        return cardHistoryRepository.findAllByEditableId(cardId);
    }

    /**
     * Удалить всю историю по изменению рейтинга.
     *
     * @return удаленная история.
     */
    public List<CardHistory> deleteAllByRate() {
        return cardHistoryRepository.deleteByColumnName(CARD_RATE);
    }

}
