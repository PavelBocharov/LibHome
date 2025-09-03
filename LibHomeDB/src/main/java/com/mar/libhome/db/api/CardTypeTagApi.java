package com.mar.libhome.db.api;

import com.mar.libhome.db.mongo.service.CardTypeTagService;
import com.mar.libhome.dto.CardTypeTagDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping(value = "/card/type/tag")
public class CardTypeTagApi {

    private final Logger log = LoggerFactory.getLogger(CardTypeTagApi.class);

    @Autowired
    private CardTypeTagService cardTypeTagService;

    @GetMapping
    public Mono<List<CardTypeTagDto>> getAll() {
        log.debug(">> get all card type tag");
        return cardTypeTagService.getAll()
                .doOnSuccess(list -> log.debug(
                        "<< get all card type tag size: {}",
                        Optional.ofNullable(list).orElse(Collections.emptyList()).size())
                )
                .doOnError(throwable -> log.error("!!! get all card type tag", throwable));
    }

    @GetMapping("/{cardTypeId}")
    public Mono<List<CardTypeTagDto>> findAllByCardTypeId(@PathVariable UUID cardTypeId) {
        log.debug(">> get all card type tag with card type id = {}", cardTypeId);
        return cardTypeTagService.findAllByCardTypeId(cardTypeId)
                .doOnSuccess(list -> log.debug(
                        "<< get all card type tag with card type id = {} size: {}",
                        cardTypeId, Optional.ofNullable(list).orElse(Collections.emptyList()).size())
                )
                .doOnError(throwable -> log.error("!!! get all card type tag with card type id = " + cardTypeId, throwable));
    }

    @PostMapping
    public Mono<List<CardTypeTagDto>> save(@RequestBody List<CardTypeTagDto> dtoList) {
        log.debug(">> save card type tag: {}", dtoList);
        return cardTypeTagService.save(dtoList)
                .doOnSuccess(type -> log.debug("<< save card type tag: {}", type))
                .doOnError(throwable -> log.error("!!! save card type tag: {}", dtoList, throwable));
    }

    @DeleteMapping
    public Mono<CardTypeTagDto> delete(@RequestBody CardTypeTagDto dto) {
        log.debug(">> delete card type tag: {}", dto);
        return cardTypeTagService.deleteById(dto.getId())
                .doOnSuccess(type -> log.debug("<< delete card type tag: {}", type))
                .doOnError(throwable -> log.error("!!! delete card type tag: {}", dto, throwable));
    }

}
