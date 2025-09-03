package com.mar.ds.db.service;

import com.mar.ds.db.remote.CardTypeRemote;
import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.view.ViewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Slf4j
@Service
public class CardTypeService implements ViewRepository<CardTypeDto> {

    @Autowired
    private CardTypeRemote cardTypeRemote;

    @Override
    public List<CardTypeDto> findAll() {
        return cardTypeRemote.findAll();
    }

    @Override
    public CardTypeDto save(CardTypeDto dto) {
        List<CardTypeDto> dtoList = saveAll(Collections.singletonList(dto));
        return isEmpty(dtoList) ? null : dtoList.get(0);
    }

    public List<CardTypeDto> saveAll(List<CardTypeDto> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return cardTypeRemote.saveAll(dtoList);
    }

    @Override
    public CardTypeDto delete(CardTypeDto dto) {
        return cardTypeRemote.remove(dto);
    }

}
