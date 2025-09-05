package com.mar.libhome.db.api;

import com.mar.libhome.db.mongo.service.CardHistoryService;
import com.mar.libhome.dto.CardHistoryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/card/history")
public class CardHistoryApi {

    private final Logger log = LoggerFactory.getLogger(CardHistoryApi.class);

    @Autowired
    private CardHistoryService cardHistoryService;

    @GetMapping("/{id}")
    public Mono<List<CardHistoryDto>> getByCardId(@PathVariable UUID id) {
        log.debug(">> get card history by card id: {}", id);
        return cardHistoryService.getByCardId(id)
                .doOnSuccess(dto -> log.debug("<< get card history by card id: {}, size: {}", id, dto.size()))
                .doOnError(throwable -> log.error("!!! get card history by card id: {}", id, throwable));
    }

    @GetMapping
    public Mono<List<CardHistoryDto>> getAll() {
        log.debug(">> get all card history");
        return cardHistoryService.getAll()
                .doOnSuccess(list -> log.debug("<< get all card history size: {}", list.size()))
                .doOnError(throwable -> log.error("!!! get all card history", throwable));
    }

    @PostMapping
    public Mono<List<CardHistoryDto>> save(@RequestBody List<CardHistoryDto> dto) {
        log.debug(">> save card history: {}", dto);
        return cardHistoryService.save(dto)
                .doOnSuccess(history -> log.debug("<< create card history size: {}", history.size()))
                .doOnError(throwable -> log.error("!!! create card history: {}", dto, throwable));
    }

}
