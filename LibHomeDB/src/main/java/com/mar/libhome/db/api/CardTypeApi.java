package com.mar.libhome.db.api;

import com.mar.libhome.db.mongo.service.CardTypeService;
import com.mar.libhome.dto.CardTypeDto;
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
@RequestMapping(value = "/card/type")
public class CardTypeApi {

    private final Logger log = LoggerFactory.getLogger(CardTypeApi.class);

    @Autowired
    private CardTypeService cardTypeService;

    @GetMapping
    public Mono<List<CardTypeDto>> getAll() {
        log.debug(">> get all card type");
        return cardTypeService.getAll()
                .doOnSuccess(list -> log.debug("<< get all card type size: {}", Optional.ofNullable(list).orElse(Collections.emptyList()).size()))
                .doOnError(throwable -> log.error("!!! get all card type", throwable));
    }

    @PostMapping
    public Mono<CardTypeDto> save(@RequestBody CardTypeDto dto) {
        log.debug(">> save card type: {}", dto);
        return cardTypeService.save(dto)
                .doOnSuccess(type -> log.debug("<< save card type: {}", type))
                .doOnError(throwable -> log.error("!!! save card type: {}", dto, throwable));
    }

    @DeleteMapping
    public Mono<CardTypeDto> delete(@RequestBody CardTypeDto dto) {
        log.debug(">> delete card type: {}", dto);
        return cardTypeService.deleteById(dto.getId())
                .doOnSuccess(type -> log.debug("<< delete card type: {}", type))
                .doOnError(throwable -> log.error("!!! delete card type: {}", dto, throwable));
    }

}
