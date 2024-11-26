package com.mar.ds.db.service;

import com.mar.ds.db.diff.DiffCard;
import com.mar.ds.db.dto.CardDto;
import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardHistory;
import com.mar.ds.db.jpa.CardHistoryRepository;
import com.mar.ds.db.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.annotation.Nullable;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class CardHistoryService {

    private final CardHistoryRepository cardHistoryRepository;
    private final CardMapper cardMapper;

    public void saveCreateCard(Card card) {
        saveHistory(null, cardMapper.toDto(card));
    }

    public void saveHistory(@Nullable CardDto old, CardDto actual) {
        assert nonNull(actual);
        List<CardHistory> diff = DiffCard.compare(old, actual);
        cardHistoryRepository.saveAll(diff);
    }

    public void saveDeleteCard(CardDto card) {
        cardHistoryRepository.save(
                CardHistory.builder()
                        .editableId(card.getId())
                        .columnName("DELETED")
                        .titlePage(card.getViewType().getTitle())
                        .oldValue(card.toString())
                        .newValue("")
                        .build()
        );
    }

    public List<CardHistory> findAllByCardId(long cardId) {
        return cardHistoryRepository.findAllByEditableId(cardId);
    }

}
