package com.mar.libhome.db.api;

import com.mar.libhome.db.aop.ApiLog;
import com.mar.libhome.db.mongo.service.CardTypeService;
import com.mar.libhome.dto.CardTypeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/card/type")
public class CardTypeApi {

    @Autowired
    private CardTypeService cardTypeService;

    @ApiLog
    @GetMapping
    public List<CardTypeDto> getAllCardType() {
        return cardTypeService.getAll();
    }

    @ApiLog
    @PostMapping
    public List<CardTypeDto> saveCardTypeList(@RequestBody List<CardTypeDto> dtoList) {
        return cardTypeService.save(dtoList);
    }

    @ApiLog
    @DeleteMapping
    public CardTypeDto deleteCardType(@RequestBody CardTypeDto dto) {
        return cardTypeService.deleteById(dto.getId());
    }

}
