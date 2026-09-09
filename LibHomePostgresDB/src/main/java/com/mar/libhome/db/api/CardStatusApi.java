package com.mar.libhome.db.api;

import com.mar.libhome.db.aop.ApiLog;
import com.mar.libhome.db.service.CardStatusService;
import com.mar.libhome.dto.CardStatusDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/card/status")
public class CardStatusApi {

    @Autowired
    private CardStatusService cardStatusService;

    @ApiLog
    @GetMapping
    public List<CardStatusDto> getAllCardStatus() {
        return cardStatusService.getAll();
    }

    @ApiLog
    @GetMapping("/tech/{techId}")
    public CardStatusDto findCardStatusByTechId(@PathVariable("techId") String techId) {
        return cardStatusService.findByTechId(techId);
    }

    @ApiLog
    @GetMapping("/tech/null")
    public List<CardStatusDto> findAllCardStatusWithTechIsNull() {
        return cardStatusService.findAllWithTechIsNull();
    }

    @ApiLog
    @PostMapping
    public List<CardStatusDto> saveCardStatusList(@RequestBody List<CardStatusDto> dtoList) {
        return cardStatusService.saveAll(dtoList);
    }

    @ApiLog
    @DeleteMapping
    public CardStatusDto deleteCardStatus(@RequestBody CardStatusDto dto) {
        return cardStatusService.deleteById(dto.getId());
    }

}
