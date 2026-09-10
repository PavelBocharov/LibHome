package com.mar.libhome.db.api;

import com.mar.libhome.api.CardStatusApi;
import com.mar.libhome.db.aop.ApiLog;
import com.mar.libhome.db.mongo.service.CardStatusService;
import com.mar.libhome.dto.CardStatusDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CardStatusApiController implements CardStatusApi {

    @Autowired
    private CardStatusService cardStatusService;

    @ApiLog
    public List<CardStatusDto> getAllCardStatus() {
        return cardStatusService.getAll();
    }

    @ApiLog
    public CardStatusDto findCardStatusByTechId(@PathVariable("techId") String techId) {
        return cardStatusService.findByTechId(techId);
    }

    @ApiLog
    public List<CardStatusDto> findAllCardStatusWithTechIsNull() {
        return cardStatusService.findAllWithTechIsNull();
    }

    @ApiLog
    public List<CardStatusDto> saveCardStatusList(@RequestBody List<CardStatusDto> dtoList) {
        return cardStatusService.saveAll(dtoList);
    }

    @ApiLog
    public CardStatusDto deleteCardStatus(@RequestBody CardStatusDto dto) {
        return cardStatusService.deleteById(dto.getId());
    }

}
