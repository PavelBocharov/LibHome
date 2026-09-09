package com.mar.libhome.db.api;

import com.mar.libhome.db.aop.ApiLog;
import com.mar.libhome.db.service.CardService;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardRq;
import com.mar.libhome.dto.CardRs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/card")
@RequiredArgsConstructor
public class CardApi {

    private final CardService cardService;

    @ApiLog
    @GetMapping
    public List<CardDto> getAllCards() {
        return cardService.getAll()
                .parallelStream()
                .map(cardService::enrich)
                .toList();
    }

    @ApiLog
    @PostMapping("/search")
    public CardRs searchCard(@RequestBody CardRq rq) {
        return cardService.search(rq);
    }

    @ApiLog
    @PostMapping
    public List<CardDto> saveCards(@RequestBody List<CardDto> dtoList) {
        return cardService.save(dtoList)
                .parallelStream()
                .map(cardService::enrich)
                .toList();
    }

    @ApiLog
    @DeleteMapping
    public CardDto deleteCard(@RequestBody CardDto dto) {
        return cardService.enrich(cardService.deleteById(dto.getId()));
    }
}
