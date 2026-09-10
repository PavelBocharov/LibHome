package com.mar.libhome.db.api;

import com.mar.libhome.api.CardTypeApi;
import com.mar.libhome.db.aop.ApiLog;
import com.mar.libhome.db.service.CardTypeService;
import com.mar.libhome.dto.CardTypeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CardTypeApiController implements CardTypeApi {

    @Autowired
    private CardTypeService cardTypeService;

    @ApiLog
    public List<CardTypeDto> getAllCardType() {
        return cardTypeService.getAll();
    }

    @ApiLog
    public List<CardTypeDto> saveCardTypeList(@RequestBody List<CardTypeDto> dtoList) {
        return cardTypeService.save(dtoList);
    }

    @ApiLog
    public CardTypeDto deleteCardType(@RequestBody CardTypeDto dto) {
        return cardTypeService.deleteById(dto.getId());
    }

}
