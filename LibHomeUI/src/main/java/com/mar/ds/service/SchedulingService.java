package com.mar.ds.service;

import com.mar.ds.db.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SchedulingService {

    @Autowired
    private CardService cardService;

    @Scheduled(cron = "0 0 0 * * *")
    public void checkAndUpdateAllCards() {
        log.info("Scheduler 'checkAndUpdateAllCards' run.");
        cardService.checkAndUpdateAllCards();
        log.info("Scheduler 'checkAndUpdateAllCards' end.");
    }

}
