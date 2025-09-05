package com.mar.libhome.db.api;

import com.mar.libhome.db.mongo.service.CardService;
import com.mar.libhome.db.mongo.service.CardStatusService;
import com.mar.libhome.db.mongo.service.CardTypeService;
import com.mar.libhome.db.mongo.service.CardTypeTagService;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardRq;
import com.mar.libhome.dto.CardStatusDto;
import lombok.RequiredArgsConstructor;
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
@RequestMapping(value = "/card")
@RequiredArgsConstructor
public class CardApi {

    private final Logger log = LoggerFactory.getLogger(CardApi.class);

    private final CardService cardService;

    @GetMapping
    public Mono<List<CardDto>> getAll() {
        log.debug(">> get all card");
        return cardService.getAll()
                .flatMapIterable(cardDtos -> cardDtos)
                .map(cardService::enrich)
                .collectList()
                .doOnSuccess(list -> log.debug("<< get all card size: {}", Optional.ofNullable(list).orElse(Collections.emptyList()).size()))
                .doOnError(throwable -> log.error("!!! get all card", throwable));
    }

    @PostMapping("/search")
    public Mono<List<CardDto>> search(@RequestBody CardRq rq) {
        return cardService.search(rq)
                .flatMapIterable(cardDtos -> cardDtos)
                .map(cardService::enrich)
                .collectList()
                .doOnSuccess(list -> log.debug("<< search card by rq({}) size: {}", rq, Optional.ofNullable(list).orElse(Collections.emptyList()).size()))
                .doOnError(throwable -> log.error("!!! search card by rq({})", rq, throwable));
    }

    @PostMapping
    public Mono<List<CardDto>> save(@RequestBody List<CardDto> dtoList) {
        return cardService.save(dtoList)
                .flatMapIterable(cardDtos -> cardDtos)
                .map(cardService::enrich)
                .collectList()
                .doOnSuccess(cards -> log.debug("<< save cards size: {}", cards.size()))
                .doOnError(throwable -> log.error("!!! save cards: {}", dtoList, throwable));
    }

    @DeleteMapping
    public Mono<CardDto> delete(@RequestBody CardDto dto) {
        return cardService.deleteById(dto.getId())
                .map(cardService::enrich)
                .doOnSuccess(card -> log.debug("<< delete card: {}", card))
                .doOnError(throwable -> log.error("!!! delete card: {}", dto, throwable));
    }
}
