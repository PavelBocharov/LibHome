package com.mar.libhome.api;

import com.mar.libhome.dto.CardStatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(
        name = "card-status-api",
        url = "${db.card.url}"
)
@RequestMapping(value = "/card/status")
public interface CardStatusApi {

    @GetMapping
    List<CardStatusDto> getAllCardStatus();

    @GetMapping("/tech/{techId}")
    CardStatusDto findCardStatusByTechId(@PathVariable("techId") String techId);

    @GetMapping("/tech/null")
    List<CardStatusDto> findAllCardStatusWithTechIsNull();

    @PostMapping
    List<CardStatusDto> saveCardStatusList(@RequestBody List<CardStatusDto> dtoList);

    @DeleteMapping
    CardStatusDto deleteCardStatus(@RequestBody CardStatusDto dto);

}