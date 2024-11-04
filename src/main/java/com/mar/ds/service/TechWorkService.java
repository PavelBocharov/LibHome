package com.mar.ds.service;

import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.LibHomeSequence;
import com.mar.ds.db.entity.TechWork;
import com.mar.ds.db.jpa.CardStatusRepository;
import com.mar.ds.db.jpa.LibHomeSeqRepository;
import com.mar.ds.db.jpa.TechWorkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.annotation.PostConstruct;

@Slf4j
@Service
public class TechWorkService {

    public static final String CARD_STATUS_ORDER_SEQ_NAME = "card-status-order";

    public static final String TECH_HASE_UPD_ID = "CARD_HAS_UPD";

    @Autowired
    private TechWorkRepository techWorkRepository;

    @Autowired
    private CardStatusRepository cardStatusRepository;

    @Autowired
    private LibHomeSeqRepository libHomeSeqRepository;

    @PostConstruct
    public void techWork() {
        long lastTechId = techWorkRepository.findWithMaxTechId().orElse(0L);
        log.debug("Get last tech ID: {}", lastTechId);
        if (lastTechId < 1) {
            log.debug("Crate tech card status 'Has UPD'...");
            lastTechId = createTechStatus_HaseUpd();
            log.debug("Crate tech card status 'Has UPD'. END.");
        }
        if (lastTechId < 2) {
            log.debug("Update card status ...");
            lastTechId = updateCardsStatus();
            log.debug("Update card status. END.");
        }

    }

    private long createTechStatus_HaseUpd() {
        CardStatus cardStatus = cardStatusRepository.save(
                CardStatus.builder()
                        .tech(TECH_HASE_UPD_ID)
                        .title("Has UPD")
                        .icon("BELL")
                        .isRate(true)
                        .color("#0B6623")
                        .order(-1L)
                        .build()
        );
        techWorkRepository.save(
                TechWork.builder()
                        .title("Create tech card status 'Has UPD' with id = " + cardStatus.getId())
                        .techId(1L)
                        .build()
        );
        return 1L;
    }

    @Transactional
    private long updateCardsStatus() {
        LibHomeSequence orderSortSeq = libHomeSeqRepository.findBySeqName(CARD_STATUS_ORDER_SEQ_NAME).orElse(null);
        if (orderSortSeq == null) {
            libHomeSeqRepository.save(LibHomeSequence.builder().seqName(CARD_STATUS_ORDER_SEQ_NAME).build());
            orderSortSeq = libHomeSeqRepository.findBySeqName(CARD_STATUS_ORDER_SEQ_NAME).orElseThrow();
        }
        long order = orderSortSeq.getSeqValue();

        List<CardStatus> cardStatusList = cardStatusRepository.findAll();
        for (CardStatus cardStatus : cardStatusList) {
            if (cardStatus.getOrder() == null) {
                cardStatus.setOrder(order);
                order += 10;
            }
        }
        cardStatusRepository.saveAll(cardStatusList);

        orderSortSeq.setSeqValue(order);
        libHomeSeqRepository.save(orderSortSeq);

        techWorkRepository.save(
                TechWork.builder()
                        .title("Update card status.")
                        .techId(2L)
                        .build()
        );
        return 2;
    }

}
