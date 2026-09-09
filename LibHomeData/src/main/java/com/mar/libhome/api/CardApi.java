package com.mar.libhome.api;

import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardRq;
import com.mar.libhome.dto.CardRs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(
        name = "card-api",
        url = "${db.card.url}",
        path = "/card"
)
public interface CardApi {

    @GetMapping
    List<CardDto> getAllCards();

    @PostMapping("/search")
    CardRs searchCard(@RequestBody CardRq rq);

    @PostMapping
    List<CardDto> saveCards(@RequestBody List<CardDto> dtoList);

    @DeleteMapping
    CardDto deleteCard(@RequestBody CardDto dto);

}
