package com.mar.libhome.db.api;

import com.mar.libhome.api.CardHistoryApi;
import com.mar.libhome.db.aop.ApiLog;
import com.mar.libhome.db.service.CardHistoryService;
import com.mar.libhome.dto.CardHistoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class CardHistoryApiController implements CardHistoryApi {

    @Autowired
    private CardHistoryService cardHistoryService;

    @ApiLog
    public List<CardHistoryDto> getCardHistoryByCardId(@PathVariable UUID id) {
        return cardHistoryService.getByCardId(id);
    }

    @ApiLog
    public List<CardHistoryDto> getAllCardHistory() {
        return cardHistoryService.getAll();
    }

    @ApiLog
    public List<CardHistoryDto> saveCardHistory(@RequestBody List<CardHistoryDto> dto) {
        return cardHistoryService.save(dto);
    }

}
