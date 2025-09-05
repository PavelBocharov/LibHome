package com.mar.ds.db.remote;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardRq;
import com.mar.libhome.dto.CardStatusDto;
import com.mar.libhome.dto.CardTypeDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.mar.libhome.utils.RestApiUtils.delete;
import static com.mar.libhome.utils.RestApiUtils.fromJson;
import static com.mar.libhome.utils.RestApiUtils.get;
import static com.mar.libhome.utils.RestApiUtils.getUri;
import static com.mar.libhome.utils.RestApiUtils.post;
import static com.mar.libhome.utils.RestApiUtils.toJson;

@Slf4j
@Service
public class CardRemote {

    public static final String REMOTE_API = "/card";

    @Value("${libhome.db.url}")
    private String host;

    @Value("${libhome.db.port}")
    private Integer port;

    public List<CardDto> findAll() {
        String url = getUri(host, port) + REMOTE_API;
        log.debug(">> Find all from: {}.", url);
        String rsJson = get(url);
        log.debug("<< Find all from: {}. RS: {}", url, rsJson);
        List<CardDto> rs = fromJson(rsJson, new TypeReference<List<CardDto>>() {
        });
        log.debug("<< Find all from: {}. List mapping rs: {}", url, rs);
        return rs;
    }

    public List<CardDto> saveAll(List<CardDto> dtoList) {
        String rqJson = toJson(dtoList);
        String url = getUri(host, port) + REMOTE_API;
        log.debug(">> Save list uri: {}, rq: {}", url, rqJson);
        String rsJson = post(url, rqJson);
        log.debug("<< Save list uri: {}, rs: {}", url, rsJson);
        List<CardDto> rs = fromJson(rsJson, new TypeReference<List<CardDto>>() {
        });
        log.debug("<< Save list uri: {}, mapping rs: {}", url, rs);
        return rs;
    }

    public CardDto save(CardDto dto) {
        return saveAll(Collections.singletonList(dto)).get(0);
    }

    public CardDto remove(CardDto dto) {
        String rqJson = toJson(dto);
        String url = getUri(host, port) + REMOTE_API;
        log.debug(">> Delete uri: {}, rq: {}", url, rqJson);
        String rsJson = delete(url, rqJson);
        log.debug("<< Delete uri: {}, rs: {}", url, rsJson);
        CardDto rs = fromJson(rsJson, new TypeReference<CardDto>() {
        });
        log.debug("<< Delete uri: {}, mapping rs: {}", url, rs);
        return rs;
    }

    private Map<String, CardRq.SortOrder> sortOrderMap(Sort sort) {
        if (sort == null) {
            return Collections.emptyMap();
        }
        Map<String, CardRq.SortOrder> map = new HashMap<>();
        sort.forEach(order -> map.put(
                        order.getProperty(),
                        Sort.Direction.ASC.equals(order.getDirection())
                                ? CardRq.SortOrder.ASC
                                : CardRq.SortOrder.DESC
                )
        );
        return map;
    }

    public List<CardDto> findAllByView(Integer view, PageRequest pageRequest) {
        return search(CardRq.builder()
                .view(view)
                .page(pageRequest.getPageNumber())
                .size(pageRequest.getPageSize())
                .sort(sortOrderMap(pageRequest.getSort()))
                .build()
        );
    }

    public List<CardDto> findAllByViewAndLikeTitleMap(Integer view, String searchText, PageRequest pageRequest) {
        return search(CardRq.builder()
                .view(view)
                .searchText(searchText)
                .page(pageRequest.getPageNumber())
                .size(pageRequest.getPageSize())
                .sort(sortOrderMap(pageRequest.getSort()))
                .build()
        );
    }

    public List<CardDto> findWithOrderByPoint(Integer viewType) {
        return search(CardRq.builder()
                .view(viewType)
                .sort(Map.of("point", CardRq.SortOrder.DESC))
                .build()
        );
    }

    public List<CardDto> findByCardStatus(CardStatusDto cardStatus) {
        return search(CardRq.builder()
                .cardStatusId(cardStatus.getId())
                .build()
        );
    }

    public List<CardDto> findByCardType(CardTypeDto cardType) {
        return search(CardRq.builder()
                .cardTypeId(cardType.getId())
                .build()
        );
    }

    public List<CardDto> findByTagId(UUID tagId) {
        return Collections.emptyList();
    }

    private List<CardDto> search(CardRq rq) {
        String rqJson = toJson(rq);
        String url = getUri(host, port) + REMOTE_API + "/search";
        log.debug(">> Search card to uri: {}, rq: {}", url, rqJson);
        String rsJson = post(url, rqJson);
        log.debug("<< Search card rs: {}", rsJson);
        List<CardDto> rs = fromJson(rsJson, new TypeReference<List<CardDto>>() { });
        log.debug("<< Search card mapping rs: {}", rs);
        return rs;
    }

}
