package com.mar.libhome.db.api;

import com.mar.libhome.api.CardTypeTagApi;
import com.mar.libhome.db.aop.ApiLog;
import com.mar.libhome.db.mongo.service.CardTypeTagService;
import com.mar.libhome.dto.CardTypeTagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class CardTypeTagApiController implements CardTypeTagApi {

    @Autowired
    private CardTypeTagService cardTypeTagService;

    @ApiLog
    public List<CardTypeTagDto> getAllCardTypeTag() {
        return cardTypeTagService.getAll();
    }

    @ApiLog
    public List<CardTypeTagDto> findAllCardTypeTagByCardTypeId(@PathVariable UUID cardTypeId) {
        return cardTypeTagService.findAllByCardTypeId(cardTypeId);
    }

    @ApiLog
    public List<CardTypeTagDto> saveCardTypeTagList(@RequestBody List<CardTypeTagDto> dtoList) {
        return cardTypeTagService.save(dtoList);
    }

    @ApiLog
    public CardTypeTagDto deleteCardTypeTag(@RequestBody CardTypeTagDto dto) {
        return cardTypeTagService.deleteById(dto.getId());
    }

}
