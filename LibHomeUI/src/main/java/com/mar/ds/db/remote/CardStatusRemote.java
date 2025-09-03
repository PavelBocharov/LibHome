package com.mar.ds.db.remote;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mar.libhome.dto.CardStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.mar.libhome.utils.RestApiUtils.delete;
import static com.mar.libhome.utils.RestApiUtils.fromJson;
import static com.mar.libhome.utils.RestApiUtils.get;
import static com.mar.libhome.utils.RestApiUtils.getUri;
import static com.mar.libhome.utils.RestApiUtils.post;
import static com.mar.libhome.utils.RestApiUtils.toJson;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Репозиторий работы с таблицей изменений карточки.
 */
@Slf4j
@Service
public class CardStatusRemote {

    @Value("${libhome.db.url}")
    private String host;

    @Value("${libhome.db.port}")
    private Integer port;

    public List<CardStatusDto> findAll() {
        String url = getUri(host, port) + "/card/status/";
        log.debug(">> Find all card status - {}", url);
        String rsJson = get(url);
        log.debug("<< Find all card status. RS: {}", rsJson);
        List<CardStatusDto> rs = fromJson(rsJson, new TypeReference<List<CardStatusDto>>() { });
        log.debug("<< Find all card status. List mapping rs: {}", rs);
        return rs;
    }

    public List<CardStatusDto> saveAll(List<CardStatusDto> cardStatusList) {
        String rqJson = toJson(cardStatusList);
        String url = getUri(host, port) + "/card/status";
        log.debug(">> Save all card status list to uri: {}, rq: {}", url, rqJson);
        String rsJson = post(url, rqJson);
        log.debug("<< Save all card status list rs: {}", rsJson);
        List<CardStatusDto> rs = fromJson(rsJson, new TypeReference<List<CardStatusDto>>() { });
        log.debug("<< Save all card status list mapping rs: {}", rs);
        return rs;
    }

    public CardStatusDto save(CardStatusDto diff) {
        return saveAll(Collections.singletonList(diff)).get(0);
    }

    public CardStatusDto remove(CardStatusDto dto) {
        String rqJson = toJson(dto);
        String url = getUri(host, port) + "/card/status";
        log.debug(">> Delete card status to uri: {}, rq: {}", url, rqJson);
        String rsJson = delete(url, rqJson);
        log.debug("<< Delete card status rs: {}", rsJson);
        CardStatusDto rs = fromJson(rsJson, CardStatusDto.class);
        log.debug("<< Delete card status mapping rs: {}", rs);
        return rs;
    }

    public CardStatusDto findByTechId(String techId) {
        if (isBlank(techId)) {
            return null;
        }
        String url = getUri(host, port) + "/card/status/tech/" + techId;
        log.debug(">> Find card status by tech id: {} - {}", techId, url);
        String rsJson = get(url);
        log.debug("<< Find card status by tech id: {}. RS: {}", techId, rsJson);
        CardStatusDto rs = fromJson(rsJson, CardStatusDto.class);
        log.debug("<< Find card status by tech id: {}. List mapping rs: {}", techId, rs);
        return rs;
    }

    public List<CardStatusDto> findByTechIdIsNull() {
        String url = getUri(host, port) + "/card/status/tech/null";
        log.debug(">> Find card status by tech id is null - {}", url);
        String rsJson = get(url);
        log.debug("<< Find card status by tech id is null. RS: {}", rsJson);
        List<CardStatusDto> rs = fromJson(rsJson, new TypeReference<List<CardStatusDto>>() { });
        log.debug("<< Find card status by tech id is null. List mapping rs: {}", rs);
        return rs;
    }

}
