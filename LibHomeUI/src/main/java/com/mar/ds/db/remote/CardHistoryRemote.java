package com.mar.ds.db.remote;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mar.libhome.dto.CardHistoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.mar.libhome.utils.RestApiUtils.fromJson;
import static com.mar.libhome.utils.RestApiUtils.get;
import static com.mar.libhome.utils.RestApiUtils.getUri;
import static com.mar.libhome.utils.RestApiUtils.post;
import static com.mar.libhome.utils.RestApiUtils.toJson;

/**
 * Репозиторий работы с таблицей изменений карточки.
 */
@Slf4j
@Service
public class CardHistoryRemote {

    @Value("${libhome.db.url}")
    private String host;

    @Value("${libhome.db.port}")
    private Integer port;

    public List<CardHistoryDto> saveAll(List<CardHistoryDto> diff) {
        String rqJson = toJson(diff);
        String url = getUri(host, port) + "/card/history";
        log.debug(">> Save all history to uri: {}, rq: {}", url, rqJson);
        String rsJson = post(url, rqJson);
        log.debug("<< Save all history rs: {}", rsJson);
        List<CardHistoryDto> rs = fromJson(rsJson, new TypeReference<List<CardHistoryDto>>() { });
        log.debug("<< Save all history mapping rs: {}", rs);
        return rs;
    }

    public CardHistoryDto save(CardHistoryDto diff) {
        return saveAll(Collections.singletonList(diff)).get(0);
    }

    public List<CardHistoryDto> findAllByEditableId(UUID editableId) {
        String url = getUri(host, port) + "/card/history/" + editableId;
        log.debug(">> GET history by card id. Uri: {}", url);
        String rsJson = get(url);
        log.debug("<< GET history by card id. RS: {}", rsJson);
        List<CardHistoryDto> rs = fromJson(rsJson, new TypeReference<List<CardHistoryDto>>() { });
        log.debug("<< GET history by card id. History mapping rs: {}", rs);
        return rs;
    }

    @Deprecated
    public List<CardHistoryDto> deleteByColumnName(String columnName) {
        return Collections.emptyList();
    }

}
