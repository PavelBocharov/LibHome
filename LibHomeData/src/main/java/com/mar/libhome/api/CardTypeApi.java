package com.mar.libhome.api;

import com.mar.libhome.dto.CardTypeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(
        name = "card-type-api",
        url = "${db.card.url}"
)
@RequestMapping(value = "/card/type")
public interface CardTypeApi {

    @GetMapping
    List<CardTypeDto> getAllCardType();

    @PostMapping
    List<CardTypeDto> saveCardTypeList(@RequestBody List<CardTypeDto> dtoList);

    @DeleteMapping
    CardTypeDto deleteCardType(@RequestBody CardTypeDto dto);

}