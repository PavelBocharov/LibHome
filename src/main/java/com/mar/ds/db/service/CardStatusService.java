package com.mar.ds.db.service;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.jpa.CardStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
public class CardStatusService {

    public static final String TECH_HASE_UPD_ID = "CARD_HAS_UPD";

    @Lazy
    @Autowired
    private CardService cardService;

    @Autowired
    private CardStatusRepository cardStatusRepository;

    public CardStatus update(CardStatus cardStatus, CardStatus oldCardStatus) {
        cardStatus = this.save(cardStatus);

        if (oldCardStatus.getIsRate() != cardStatus.getIsRate()
                || oldCardStatus.getHasUpdStatus() != cardStatus.getHasUpdStatus()
        ) {
            cardService.checkAndUpdateAllCards();
        }

        return cardStatus;
    }

    public void delete(CardStatus cardStatus) throws Exception {
        List<Card> cards = cardService.findByCardStatus(cardStatus);
        if (isEmpty(cards)) {
            log.info("Not find cards by status: {}. Delete status.", cardStatus);
            cardStatusRepository.delete(cardStatus);
        } else {
            log.warn("Find cards by status: {}, list: {}", cardStatus, cards);
            throw new Exception(String.format("Find cards with status: '%s', count: %d.", cardStatus.getTitle(), cards.size()));
        }
    }

    public CardStatus save(CardStatus cardStatus) {
        return cardStatusRepository.save(cardStatus);
    }

    public List<CardStatus> findByWithTechIdIsNull() {
        return cardStatusRepository.findByWithTechIdIsNull();
    }

    public CardStatus findByTechId(String techId) {
        return cardStatusRepository.findByTechId(techId);
    }

    public List<CardStatus> findAll() {
        return cardStatusRepository.findAll();
    }

    public List<CardStatus> saveAll(List<CardStatus> cardStatusList) {
        return cardStatusRepository.saveAll(cardStatusList);
    }

}
