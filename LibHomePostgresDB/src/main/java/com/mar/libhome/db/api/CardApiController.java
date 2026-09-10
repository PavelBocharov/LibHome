package com.mar.libhome.db.api;

import com.mar.libhome.api.CardApi;
import com.mar.libhome.db.aop.ApiLog;
import com.mar.libhome.db.service.CardService;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardRq;
import com.mar.libhome.dto.CardRs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CardApiController implements CardApi {

    private final CardService cardService;

    @ApiLog
    public List<CardDto> getAllCards() {
        return cardService.getAll()
                .parallelStream()
                .map(cardService::enrich)
                .toList();
    }

    @ApiLog
    public CardRs searchCard(@RequestBody CardRq rq) {
        return cardService.search(rq);
    }

    @Override
    public CardRs searchCardWithoutViewByText(CardRq rq) {
        return cardService.searchCardByTextWithoutView(rq);
    }

    @ApiLog
    public List<CardDto> saveCards(@RequestBody List<CardDto> dtoList) {
        return cardService.save(dtoList)
                .parallelStream()
                .map(cardService::enrich)
                .toList();
    }

    @ApiLog
    public CardDto deleteCard(@RequestBody CardDto dto) {
        return cardService.enrich(cardService.deleteById(dto.getId()));
    }
}
