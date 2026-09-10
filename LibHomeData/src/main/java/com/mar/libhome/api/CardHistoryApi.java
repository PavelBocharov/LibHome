package com.mar.libhome.api;

import com.mar.libhome.dto.CardHistoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "card-history-api",
        url = "${db.card.url}"
)
@RequestMapping(value = "/card/history")
public interface CardHistoryApi {

    @GetMapping("/{id}")
    List<CardHistoryDto> getCardHistoryByCardId(@PathVariable UUID id);

    @GetMapping
    List<CardHistoryDto> getAllCardHistory();

    @PostMapping
    List<CardHistoryDto> saveCardHistory(@RequestBody List<CardHistoryDto> dto);

}
