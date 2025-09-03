package com.mar.ds.db.remote;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.dto.CardTypeTagDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.mar.libhome.utils.RestApiUtils.delete;
import static com.mar.libhome.utils.RestApiUtils.fromJson;
import static com.mar.libhome.utils.RestApiUtils.get;
import static com.mar.libhome.utils.RestApiUtils.getUri;
import static com.mar.libhome.utils.RestApiUtils.post;
import static com.mar.libhome.utils.RestApiUtils.toJson;

@Slf4j
@Service
public class CardTypeTagRemote {

    public static final String REMOTE_API = "/card/type/tag";
    @Value("${libhome.db.url}")
    private String host;
    @Value("${libhome.db.port}")
    private Integer port;

    public List<CardTypeTagDto> findByCardType(CardTypeDto dto) {
        String url = getUri(host, port) + REMOTE_API + "/" + dto.getId();
        log.debug(">> Find all card type tag list by type id - {}", url);
        String rsJson = get(url);
        log.debug("<< Find all card type tag list by type id. RS: {}", rsJson);
        List<CardTypeTagDto> rs = fromJson(rsJson, new TypeReference<List<CardTypeTagDto>>() { });
        log.debug("<< Find all card type tag list by type id. List mapping rs: {}", rs);
        return rs;
    }

    public List<CardTypeTagDto> findAll() {
        String url = getUri(host, port) + REMOTE_API;
        log.debug(">> Find all card type tag list - {}", url);
        String rsJson = get(url);
        log.debug("<< Find all card type tag list. RS: {}", rsJson);
        List<CardTypeTagDto> rs = fromJson(rsJson, new TypeReference<List<CardTypeTagDto>>() { });
        log.debug("<< Find all card type tag list. List mapping rs: {}", rs);
        return rs;
    }

    public List<CardTypeTagDto> save(List<CardTypeTagDto> dtoList) {
        String rqJson = toJson(dtoList);
        String url = getUri(host, port) + REMOTE_API;
        log.debug(">> Save all card type tag list to uri: {}, rq: {}", url, rqJson);
        String rsJson = post(url, rqJson);
        log.debug("<< Save all card type tag list rs: {}", rsJson);
        List<CardTypeTagDto> rs = fromJson(rsJson, new TypeReference<List<CardTypeTagDto>>() { });
        log.debug("<< Save all card type tag list mapping rs: {}", rs);
        return rs;
    }

    public CardTypeTagDto deleteById(UUID id) {
        return remove(CardTypeTagDto.builder().id(id).build());
    }

    public CardTypeTagDto remove(CardTypeTagDto dto) {
        String rqJson = toJson(dto);
        String url = getUri(host, port) + REMOTE_API;
        log.debug(">> Delete card type to uri: {}, rq: {}", url, rqJson);
        String rsJson = delete(url, rqJson);
        log.debug("<< Delete card type rs: {}", rsJson);
        CardTypeTagDto rs = fromJson(rsJson, CardTypeTagDto.class);
        log.debug("<< Delete card type mapping rs: {}", rs);
        return rs;
    }

}
