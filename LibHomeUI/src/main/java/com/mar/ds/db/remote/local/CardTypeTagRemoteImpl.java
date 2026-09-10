package com.mar.ds.db.remote.local;

import com.mar.ds.db.remote.CardTypeTagRemote;
import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.dto.CardTypeTagDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.mar.libhome.utils.RestApiUtils.toJson;

@Slf4j
@Service
@Profile("!production")
public class CardTypeTagRemoteImpl implements CardTypeTagRemote {

    public static final List<CardTypeTagDto> data = List.of(
            CardTypeTagDto.builder()
                    .id(UUID.randomUUID())
                    .title("TEST TAG")
                    .cardTypeId(CardTypeRemoteImpl.data.getId())
                    .build()
    );

    public List<CardTypeTagDto> findByCardType(CardTypeDto dto) {
        log.debug(">> Find all card type tag list by type id - LOCAL");
        List<CardTypeTagDto> rs = data;
        log.debug("<< Find all card type tag list by type id. List mapping rs: {}", rs);
        return rs;
    }

    public List<CardTypeTagDto> findAll() {
        log.debug(">> Find all card type tag list - LOCAL");
        List<CardTypeTagDto> rs = data;
        log.debug("<< Find all card type tag list. List mapping rs: {}", rs);
        return rs;
    }

    public List<CardTypeTagDto> save(List<CardTypeTagDto> dtoList) {
        String rqJson = toJson(dtoList);
        log.debug(">> Save all card type tag list to uri: LOCAL, rq: {}", rqJson);
        List<CardTypeTagDto> rs = data;
        log.debug("<< Save all card type tag list mapping rs: {}", rs);
        return rs;
    }

    public CardTypeTagDto deleteById(UUID id) {
        return remove(CardTypeTagDto.builder().id(id).build());
    }

    public CardTypeTagDto remove(CardTypeTagDto dto) {
        String rqJson = toJson(dto);
        log.debug(">> Delete card type to uri: LOCAL, rq: {}", rqJson);
        CardTypeTagDto rs = data.get(0);
        log.debug("<< Delete card type mapping rs: {}", rs);
        return rs;
    }

}
