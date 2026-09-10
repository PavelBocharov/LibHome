package com.mar.libhome.api;

import com.mar.libhome.dto.CardTypeTagDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "card-type-tag-api",
        url = "${db.card.url}"
)
@RequestMapping(value = "/card/type/tag")
public interface CardTypeTagApi {

    @GetMapping
    List<CardTypeTagDto> getAllCardTypeTag();

    @GetMapping("/{cardTypeId}")
    List<CardTypeTagDto> findAllCardTypeTagByCardTypeId(@PathVariable UUID cardTypeId);

    @PostMapping
    List<CardTypeTagDto> saveCardTypeTagList(@RequestBody List<CardTypeTagDto> dtoList);

    @DeleteMapping
    CardTypeTagDto deleteCardTypeTag(@RequestBody CardTypeTagDto dto);
}