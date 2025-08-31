package com.mar.ds.db.service;

import com.mar.ds.db.remote.CardStatusRemote;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
public class CardStatusService {

    public static final String TECH_HASE_UPD_ID = "CARD_HAS_UPD";

    @Lazy
    @Autowired
    private CardService cardService;

    @Autowired
    private CardStatusRemote cardStatusRemote;

    public CardStatusDto update(CardStatusDto cardStatus, CardStatusDto oldCardStatus) {
        cardStatus = this.save(cardStatus);

        if (oldCardStatus.getIsRate() != cardStatus.getIsRate()
                || oldCardStatus.getHasUpdStatus() != cardStatus.getHasUpdStatus()
        ) {
            cardService.checkAndUpdateAllCards();
        }

        return cardStatus;
    }

    public void remove(CardStatusDto cardStatus) throws Exception {
        List<CardDto> cards = cardService.findByCardStatus(CardStatusDto.builder()
                        .id(cardStatus.getId())
                        .color(cardStatus.getColor())
                        .tech(cardStatus.getTech())
                        .hasUpdStatus(cardStatus.getHasUpdStatus())
                        .icon(cardStatus.getIcon())
                        .title(cardStatus.getTitle())
                        .isRate(cardStatus.getIsRate())
                        .order(cardStatus.getOrder())
                .build());
        if (isEmpty(cards)) {
            log.info("Not find cards by status: {}. Delete status.", cardStatus);
            cardStatusRemote.remove(cardStatus);
        } else {
            log.warn("Find cards by status: {}, list: {}", cardStatus, cards);
            throw new Exception(
                    String.format("Find cards with status: '%s', count: %d.", cardStatus.getTitle(), cards.size())
            );
        }
    }

    public CardStatusDto save(CardStatusDto cardStatus) {
        List<CardStatusDto> dtoList = saveAll(of(cardStatus));
        if (isNotEmpty(dtoList)) {
            return dtoList.get(0);
        }
        return null;
    }

    public List<CardStatusDto> findByWithTechIdIsNull() {
        return cardStatusRemote.findByTechIdIsNull();
    }

    public CardStatusDto findByTechId(String techId) {
        return cardStatusRemote.findByTechId(techId);
    }

    public List<CardStatusDto> findAll() {
        return cardStatusRemote.findAll();
    }

    public List<CardStatusDto> saveAll(List<CardStatusDto> cardStatusList) {
        return cardStatusRemote.saveAll(cardStatusList);
    }

}
