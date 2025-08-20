package com.mar.ds.service;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.LibHomeSequence;
import com.mar.ds.db.entity.TechWork;
import com.mar.ds.db.jpa.LibHomeSeqRepository;
import com.mar.ds.db.jpa.TechWorkRepository;
import com.mar.ds.db.remote.TechApiRemote;
import com.mar.ds.db.service.CardHistoryService;
import com.mar.ds.db.service.CardService;
import com.mar.ds.db.service.CardStatusService;
import com.mar.ds.db.service.MigrationToMongoService;
import com.mar.libhome.dto.CardStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.annotation.PostConstruct;

import static com.mar.ds.db.service.CardStatusService.TECH_HASE_UPD_ID;

@Slf4j
@Service
public class TechWorkService {

    public static final String CARD_STATUS_ORDER_SEQ_NAME = "card-status-order";

    @Autowired
    private TechWorkRepository techWorkRepository;

    @Autowired
    private CardStatusService cardStatusService;

    @Autowired
    private CardService cardService;

    @Autowired
    private CardHistoryService historyService;

    @Autowired
    private LibHomeSeqRepository libHomeSeqRepository;

    @Autowired
    private MigrationToMongoService migrateToMongoService;

    @Autowired
    private TechApiRemote techApiRemote;

    @PostConstruct
    public void techWork() {
        while (true) {
            try {
                Thread.sleep(5_000L);
                techApiRemote.checkHealth();
                break;
            } catch (Exception e) {
                log.warn("BD not startup - {}", e.getMessage());
            }
        }

        long lastTechId = techWorkRepository.findWithMaxTechId().orElse(0L);
        log.debug("Get last tech ID: {}", lastTechId);
        if (lastTechId < 1) {
            log.debug("Crate tech card status 'Has UPD'...");
            lastTechId = createTechStatus_HaseUpd();
            log.debug("Crate tech card status 'Has UPD'. END.");
        }
        if (lastTechId < 2) {
            log.debug("Update card orders ...");
            lastTechId = updateCardsOrder();
            log.debug("Update card orders. END.");
        }
        if (lastTechId < 3) {
            log.debug("Update card status ...");
            lastTechId = updateCardStatus();
            log.debug("Update card status. END.");
        }
        if (lastTechId < 4) {
            log.debug("Fix not upd card status and calc rate ...");
            lastTechId = fixCannotUpdCardStatus();
            log.debug("Fix not upd card status. END.");
        }
        if (lastTechId < 5) {
            log.debug("Remove rate history...");
            lastTechId = removeRateHistory();
            log.debug("Remove rate history. END.");
        }
        if (lastTechId < 6) {
            log.debug("Move history to MongoDB...");
            lastTechId = moveHistory();
            log.debug("Move history to MongoDB. END.");
        }
        if (lastTechId < 8) {
            log.debug("Move status list to MongoDB...");
            lastTechId = moveStatus();
            log.debug("Move status list to MongoDB. END.");
        }
    }

    private long createTechStatus_HaseUpd() {
        CardStatusDto cardStatus = cardStatusService.save(
                CardStatusDto.builder()
                        .tech(TECH_HASE_UPD_ID)
                        .title("Has UPD")
                        .icon("BELL")
                        .isRate(true)
                        .color("#0B6623")
                        .order(-1L)
                        .hasUpdStatus(false)
                        .build()
        );
        techWorkRepository.save(
                TechWork.builder()
                        .title("Create tech card status 'Has UPD' with id = " + cardStatus.getLongId())
                        .techId(1L)
                        .build()
        );
        return 1L;
    }

    @Transactional
    private long updateCardsOrder() {
        LibHomeSequence orderSortSeq = libHomeSeqRepository.findBySeqName(CARD_STATUS_ORDER_SEQ_NAME).orElse(null);
        if (orderSortSeq == null) {
            libHomeSeqRepository.save(LibHomeSequence.builder().seqName(CARD_STATUS_ORDER_SEQ_NAME).build());
            orderSortSeq = libHomeSeqRepository.findBySeqName(CARD_STATUS_ORDER_SEQ_NAME).orElseThrow();
        }
        long order = orderSortSeq.getSeqValue();

        List<CardStatusDto> cardStatusList = cardStatusService.findAll();
        for (CardStatusDto cardStatus : cardStatusList) {
            if (cardStatus.getOrder() == null) {
                cardStatus.setOrder(order);
                order += 10;
            }
        }
        cardStatusService.saveAll(cardStatusList);

        orderSortSeq.setSeqValue(order);
        libHomeSeqRepository.save(orderSortSeq);

        techWorkRepository.save(
                TechWork.builder()
                        .title("Update card orders.")
                        .techId(2L)
                        .build()
        );
        return 2;
    }

    @Transactional
    private long updateCardStatus() {
//      Fix text
        TechWork techWork = techWorkRepository.findByTechId(2L).orElseThrow();
        techWork.setTitle("Update card orders.");
        techWorkRepository.save(techWork);
//      ----------------

        CardStatusDto hasUpdStatus = cardStatusService.findByTechId(TECH_HASE_UPD_ID);
        List<Card> cards = cardService.findAll();
        for (Card card : cards) {
            if (card.getLastUpdate().after(card.getLastGame())) {
                card.setOldCardStatus(card.getCardStatus());
                card.setCardStatus(
                        CardStatus.builder()
                                .id(hasUpdStatus.getId().getLeastSignificantBits())
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
        }
        cardService.saveAll(cards);

        techWorkRepository.save(
                TechWork.builder()
                        .title("Update card status.")
                        .techId(3L)
                        .build()
        );
        return 3L;
    }

    private long fixCannotUpdCardStatus() {
        cardService.checkAndUpdateAllCards();

        techWorkRepository.save(
                TechWork.builder()
                        .title("Fix not upd card status and calc rate.")
                        .techId(4L)
                        .build()
        );
        return 4L;
    }

    private long removeRateHistory() {
        historyService.deleteAllByRate();

        techWorkRepository.save(
                TechWork.builder()
                        .title("Remove rate history.")
                        .techId(5L)
                        .build()
        );
        return 5L;
    }

    private long moveHistory() {
        migrateToMongoService.moveHistory();
        techWorkRepository.save(
                TechWork.builder()
                        .title("Move history to MongoDB.")
                        .techId(6L)
                        .build()
        );
        return 6L;
    }

    private long moveStatus() {
        migrateToMongoService.moveStatus();
        techWorkRepository.save(
                TechWork.builder()
                        .title("Move status list to MongoDB (good).")
                        .techId(8L)
                        .build()
        );
        return 8L;
    }

}
