package com.mar.ds.db.remote;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mar.libhome.dto.CardTypeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mar.libhome.utils.RestApiUtils.delete;
import static com.mar.libhome.utils.RestApiUtils.fromJson;
import static com.mar.libhome.utils.RestApiUtils.get;
import static com.mar.libhome.utils.RestApiUtils.getUri;
import static com.mar.libhome.utils.RestApiUtils.post;
import static com.mar.libhome.utils.RestApiUtils.toJson;

@Slf4j
@Service
public class CardTypeRemote {

    public static final String REMOTE_API = "/card/type";

    @Value("${libhome.db.url}")
    private String host;

    @Value("${libhome.db.port}")
    private Integer port;

    public List<CardTypeDto> findAll() {
        String url = getUri(host, port) + REMOTE_API;
        log.debug(">> Find all card type list - {}", url);
        String rsJson = get(url);
        log.debug("<< Find all card type list. RS: {}", rsJson);
        List<CardTypeDto> rs = fromJson(rsJson, new TypeReference<List<CardTypeDto>>() { });
        log.debug("<< Find all card type list. List mapping rs: {}", rs);
        return rs;
    }

    public List<CardTypeDto> saveAll(List<CardTypeDto> dtoList) {
        String rqJson = toJson(dtoList);
        String url = getUri(host, port) + REMOTE_API;
        log.debug(">> Save all card type list to uri: {}, rq: {}", url, rqJson);
        String rsJson = post(url, rqJson);
        log.debug("<< Save all card type list rs: {}", rsJson);
        List<CardTypeDto> rs = fromJson(rsJson, new TypeReference<List<CardTypeDto>>() { });
        log.debug("<< Save all card type list mapping rs: {}", rs);
        return rs;
    }

    public CardTypeDto remove(CardTypeDto dto) {
        String rqJson = toJson(dto);
        String url = getUri(host, port) + REMOTE_API;
        log.debug(">> Delete card type to uri: {}, rq: {}", url, rqJson);
        String rsJson = delete(url, rqJson);
        log.debug("<< Delete card type rs: {}", rsJson);
        CardTypeDto rs = fromJson(rsJson, CardTypeDto.class);
        log.debug("<< Delete card type mapping rs: {}", rs);
        return rs;
    }

}
