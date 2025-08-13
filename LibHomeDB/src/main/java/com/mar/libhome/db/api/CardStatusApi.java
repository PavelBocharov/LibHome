package com.mar.libhome.db.api;

import com.mar.libhome.db.mongo.service.CardStatusService;
import com.mar.libhome.dto.CardStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/card/status")
public class CardStatusApi {

    private final Logger log = LoggerFactory.getLogger(CardStatusApi.class);

    @Autowired
    private CardStatusService cardStatusService;

    @GetMapping
    public Mono<List<CardStatusDto>> getAll() {
        log.debug(">> get all card status");
        return cardStatusService.getAll()
                .doOnSuccess(list -> log.debug("<< get all card status size: {}", Optional.ofNullable(list).orElse(Collections.emptyList()).size()))
                .doOnError(throwable -> log.error("!!! get all card status", throwable));
    }

    @PostMapping
    public Mono<CardStatusDto> save(@RequestBody CardStatusDto dto) {
        log.debug(">> save card status: {}", dto);
        return cardStatusService.save(dto)
                .doOnSuccess(status -> log.debug("<< save card status: {}", status))
                .doOnError(throwable -> log.error("!!! save card status: {}", dto, throwable));
    }

    @DeleteMapping
    public Mono<CardStatusDto> delete(@RequestBody CardStatusDto dto) {
        log.debug(">> delete card status: {}", dto);
        return cardStatusService.deleteById(dto.getId())
                .doOnSuccess(status -> log.debug("<< delete card status: {}", status))
                .doOnError(throwable -> log.error("!!! delete card status: {}", dto, throwable));
    }

}
