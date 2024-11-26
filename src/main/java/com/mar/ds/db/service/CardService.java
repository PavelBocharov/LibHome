package com.mar.ds.db.service;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.CardType;
import com.mar.ds.db.entity.ViewType;
import com.mar.ds.db.jpa.CardHistoryRepository;
import com.mar.ds.db.jpa.CardRepository;
import com.mar.ds.db.mapper.CardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

import static com.mar.ds.db.service.CardStatusService.TECH_HASE_UPD_ID;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardStatusService cardStatusService;

    @Autowired
    public CardService(CardRepository cardRepository, @Lazy CardStatusService cardStatusService, CardHistoryRepository cardHistoryRepository, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.cardStatusService = cardStatusService;
    }

    public void checkAndUpdateAllCards() {
        List<Card> cards = cardRepository.findAll();

        ArrayList<Card> forUpd = new ArrayList<>(cards.size());
        for (Card card : cards) {
            forUpd.add(checkCard(card));
        }
        saveAll(forUpd);
    }

    public Page<Card> findAllByView(@NotNull ViewType view, Pageable pageable) {
        return cardRepository.findAllByView(view, pageable);
    }

    public Page<Card> findAllByViewAndLikeTitleMap(@NotNull ViewType view, String searchText, Pageable pageable) {
        return cardRepository.findAllByViewAndLikeTitleMap(view, searchText, pageable);
    }

    public Card checkCard(Card card) {
        assert nonNull(card);
        assert nonNull(card.getCardStatus());

        // update status
        CardStatus status = card.getCardStatus();
        CardStatus oldStatus = card.getOldCardStatus();

        if (nonNull(card.getLastGame()) && nonNull(card.getLastUpdate())
                && card.getLastUpdate().after(card.getLastGame())
        ) {
            if (!status.isTech() && TRUE.equals(status.getHasUpdStatus())) {
                CardStatus hasUpdStatus = cardStatusService.findByTechId(TECH_HASE_UPD_ID);
                card.setOldCardStatus(status);
                card.setCardStatus(hasUpdStatus);
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

    public Card save(Card card) {
        return cardRepository.save(checkCard(card));
    }

    public List<Card> saveAll(Collection<Card> cards) {
        List<Card> savedCards = new ArrayList<>(cards.size());
        for (Card card : cards) {
            savedCards.add(save(card));
        }
        return savedCards;
    }

    public List<Card> findAll() {
        return cardRepository.findAll();
    }

    public void delete(Card card) {
        cardRepository.delete(card);
    }

    public List<Card> findWithOrderByPoint(ViewType viewType) {
        return cardRepository.findWithOrderByPoint(viewType);
    }

    public List<Card> findByCardStatus(CardStatus cardStatus) {
        return cardRepository.findByCardStatus(cardStatus);
    }

    public List<Card> findByTag(Long tagId) {
        return cardRepository.findByTagIn(tagId);
    }

    public List<Card> findByCardType(CardType cardType) {
        return cardRepository.findByCardType(cardType);
    }

    private void calcRate(Card card) {
        if (!Optional.ofNullable(card.getCardStatus().getIsRate()).orElse(false)) {
            card.setRate(0.0);
            return;
        }

        long deltaGame = -1;
        if (card.getLastGame() != null) {
            long now = new Date().getTime() / 86400000;
            long lastGameTime = card.getLastGame().getTime() / 86400000;
            deltaGame = now - lastGameTime;
        }

        card.setRate(card.getPoint() * deltaGame * 0.01);
    }
}
