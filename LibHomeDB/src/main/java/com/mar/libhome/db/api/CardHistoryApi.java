package com.mar.libhome.db.api;

import com.mar.libhome.db.aop.ApiLog;
import com.mar.libhome.db.mongo.service.CardHistoryService;
import com.mar.libhome.dto.CardHistoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/card/history")
public class CardHistoryApi {

    @Autowired
    private CardHistoryService cardHistoryService;

    @ApiLog
    @GetMapping("/{id}")
    public List<CardHistoryDto> getCardHistoryByCardId(@PathVariable UUID id) {
        return cardHistoryService.getByCardId(id);
    }

    @ApiLog
    @GetMapping
    public List<CardHistoryDto> getAllCardHistory() {
        return cardHistoryService.getAll();
    }

    @ApiLog
    @PostMapping
    public List<CardHistoryDto> saveCardHistory(@RequestBody List<CardHistoryDto> dto) {
        return cardHistoryService.save(dto);
    }

}
