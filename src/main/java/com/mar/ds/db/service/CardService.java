package com.mar.ds.db.service;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.CardType;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.db.entity.ViewType;
import com.mar.ds.db.jpa.CardRepository;
import com.mar.ds.db.jpa.CardStatusRepository;
import com.mar.ds.views.card.CardView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import static com.mar.ds.service.TechWorkService.TECH_HASE_UPD_ID;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardStatusRepository cardStatusRepository;

    public Card save(Card card) {
        return cardRepository.save(checkCard(card));
    }

    public List<Card> saveAll(Collection<Card> card) {
        return cardRepository.saveAll(card.stream().map(this::checkCard).toList());
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

    public Page<CardView.CardViewData> findAllByView(@NotNull ViewType view, Pageable pageable) {
        Page<Map<String, Object>> cards = cardRepository.findAllByView(view, pageable);

        ArrayList<CardView.CardViewData> cardViewList = new ArrayList<>(cards.getContent().size());
        for (Map<String, Object> cardInfo : cards.getContent()) {
            cardViewList.add(new CardView.CardViewData(
                    (Card) cardInfo.get("crd"),
                    Float.parseFloat(String.valueOf(cardInfo.get("rate")))
            ));
        }

        return new PageImpl<>(cardViewList, cards.getPageable(), cards.getTotalElements());
    }

    public Page<CardView.CardViewData> findAllByViewAndLikeTitleMap(@NotNull ViewType view, String searchText, Pageable pageable) {
        Page<Map<String, Object>> cards = cardRepository.findAllByViewAndLikeTitleMap(view, searchText, pageable);

        ArrayList<CardView.CardViewData> cardViewList = new ArrayList<>(cards.getContent().size());
        for (Map<String, Object> cardInfo : cards.getContent()) {
            cardViewList.add(new CardView.CardViewData(
                    (Card) cardInfo.get("crd"),
                    Float.parseFloat(String.valueOf(cardInfo.get("rate")))
            ));
        }

        return new PageImpl<>(cardViewList, cards.getPageable(), cards.getTotalElements());
    }

    private Card checkCard(Card card) {
        assert nonNull(card);

        if (nonNull(card.getLastGame()) && nonNull(card.getLastUpdate())
                && card.getLastUpdate().after(card.getLastGame())
        ) {
            CardStatus hasUpdStatus = cardStatusRepository.findByTechId(TECH_HASE_UPD_ID);
            card.setOldCardStatus(card.getCardStatus());
            card.setCardStatus(hasUpdStatus);
        } else {
            if (!isBlank(card.getCardStatus().getTech())) {
                card.setCardStatus(card.getOldCardStatus());
                card.setOldCardStatus(null);
            }
        }

        return card;
    }
}
