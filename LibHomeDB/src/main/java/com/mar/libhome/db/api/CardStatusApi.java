package com.mar.libhome.db.api;

import com.mar.libhome.db.mongo.service.CardStatusService;
import com.mar.libhome.dto.CardStatusDto;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

    @GetMapping("/tech/{techId}")
    public Mono<CardStatusDto> findByTechId(@PathVariable("techId") String techId) {
        log.debug(">> get card status by tech id: {}", techId);
        return cardStatusService.findByTechId(techId)
                .doOnSuccess(dto -> log.debug("<< get card status by tech id: {}. Data: {}", techId, dto))
                .doOnError(throwable -> log.error("!!! get card status by tech id: {}", techId, throwable));
    }

    @GetMapping("/tech/null")
    public Mono<List<CardStatusDto>> findAllWithTechIsNull() {
        log.debug(">> find card list status with tech id is null");
        return cardStatusService.findAllWithTechIsNull()
                .doOnSuccess(status -> log.debug("<< find card list status with tech id is null: {}", status))
                .doOnError(throwable -> log.error("!!! find card list status with tech id is null", throwable));
    }

    @PostMapping
    public Mono<List<CardStatusDto>> save(@RequestBody List<CardStatusDto> dtoList) {
        log.debug(">> save card status list size: {}", dtoList.size());
        return cardStatusService.saveAll(dtoList)
                .doOnSuccess(status -> log.debug("<< save card list status: {}", status))
                .doOnError(throwable -> {
                    log.error("!!! save card status list size: {}. Msg: {}. LocalMSg: {}", dtoList.size(), throwable.getMessage(), throwable.getLocalizedMessage());
                    Mono.error(throwable);
                });
    }

    @DeleteMapping
    public Mono<CardStatusDto> delete(@RequestBody CardStatusDto dto) {
        log.debug(">> delete card status: {}", dto);
        return cardStatusService.deleteById(dto.getId())
                .doOnSuccess(status -> log.debug("<< delete card status: {}", status))
                .doOnError(throwable -> log.error("!!! delete card status: {}", dto, throwable));
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<String> handleCustomError(Exception ex) {
        log.debug("handleCustomError: {}", ex.getMessage());
        return ResponseEntity
                .status(500)
                .body(ExceptionUtils.getMessage(ex));
    }

}
