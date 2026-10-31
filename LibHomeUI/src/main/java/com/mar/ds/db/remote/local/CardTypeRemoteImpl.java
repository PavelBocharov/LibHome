package com.mar.ds.db.remote.local;

import com.mar.ds.db.remote.CardTypeRemote;
import com.mar.libhome.dto.CardTypeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.mar.libhome.utils.RestApiUtils.toJson;

@Slf4j
@Service
@Profile("!production")
public class CardTypeRemoteImpl implements CardTypeRemote {

    public static final CardTypeDto data = CardTypeDto.builder()
            .id(UUID.randomUUID())
            .title("TEST TYPE")
            .build();

    public List<CardTypeDto> findAll() {
        log.debug(">> Find all card type list - LOCAL");
        List<CardTypeDto> rs = List.of(data);
        log.debug("<< Find all card type list. List mapping rs: {}", rs);
        return rs;
    }

    public List<CardTypeDto> saveAll(List<CardTypeDto> dtoList) {
        String rqJson = toJson(dtoList);
        log.debug(">> Save all card type list to uri: LOCAL, rq: {}", rqJson);
        List<CardTypeDto> rs = findAll();
        log.debug("<< Save all card type list mapping rs: {}", rs);
        return rs;
    }

    public CardTypeDto remove(CardTypeDto dto) {
        String rqJson = toJson(dto);
        log.debug(">> Delete card type to uri: LOCAL, rq: {}", rqJson);
        CardTypeDto rs = findAll().get(0);
        log.debug("<< Delete card type mapping rs: {}", rs);
        return rs;
    }

}
