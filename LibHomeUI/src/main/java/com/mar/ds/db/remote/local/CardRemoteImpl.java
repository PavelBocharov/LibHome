package com.mar.ds.db.remote.local;

import com.mar.ds.db.remote.CardRemote;
import com.mar.libhome.api.data.PageRequest;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardRq;
import com.mar.libhome.dto.CardRs;
import com.mar.libhome.dto.CardStatusDto;
import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.enums.GameEngine;
import com.mar.libhome.enums.Language;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.mar.libhome.utils.RestApiUtils.toJson;

@Slf4j
@Service
@Profile("!production")
public class CardRemoteImpl implements CardRemote {

    public static final List<CardDto> data = List.of(
            CardDto.builder()
                    .id(UUID.randomUUID())
                    .title("title")
                    .info("info")
                    .rate(5.0)
                    .cardStatus(
                            CardStatusRemoteImpl.data
                    )
                    .cardType(
                            CardTypeRemoteImpl.data
                    )
                    .lastUpdate(new Date())
                    .engine(GameEngine.DREAMCAST)
                    .language(Language.FI)
                    .tagList(CardTypeTagRemoteImpl.data)
                    .point(5.0)
                    .viewType(1)
                    .lastGame(new Date())
                    .build()
    );

    public List<CardDto> findAll() {
        log.debug(">> Find all from LOCAL.");
        List<CardDto> rs = data;
        log.debug("<< Find all from: LOCAL. List mapping rs: {}", rs);
        return rs;
    }

    public CardRs findAll(PageRequest pageRequest) {
        return search(CardRq.builder()
                .page(pageRequest.getPageNumber())
                .size(pageRequest.getPageSize())
                .sort(sortOrderMap(pageRequest.getSort()))
                .build()
        );
    }

    public List<CardDto> saveAll(List<CardDto> dtoList) {
        String rqJson = toJson(dtoList);
        log.debug(">> Save list uri: LOCAL, rq: {}", rqJson);
        List<CardDto> rs = data;
        log.debug("<< Save list uri: LOCAL, mapping rs: {}", rs);
        return rs;
    }

    public CardDto save(CardDto dto) {
        return saveAll(Collections.singletonList(dto)).get(0);
    }

    public CardDto remove(CardDto dto) {
        String rqJson = toJson(dto);
        log.debug(">> Delete uri: LOCAL, rq: {}", rqJson);
        return data.get(0);
    }

    private Map<String, CardRq.SortOrder> sortOrderMap(PageRequest.Sort sort) {
        if (sort == null) {
            return Collections.emptyMap();
        }
        Map<String, CardRq.SortOrder> map = new HashMap<>();

        for (String property : sort.order().keySet()) {
            map.put(
                    property,
                    PageRequest.Sort.Direction.ASC.equals(sort.order().get(property))
                            ? CardRq.SortOrder.ASC
                            : CardRq.SortOrder.DESC
            );
        }
        return map;
    }

    public CardRs findAllByView(Integer view, PageRequest pageRequest) {
        return search(CardRq.builder()
                .view(view)
                .page(pageRequest.getPageNumber())
                .size(pageRequest.getPageSize())
                .sort(sortOrderMap(pageRequest.getSort()))
                .build()
        );
    }

    public CardRs findAllByViewAndLikeTitleMap(Integer view, String searchText, PageRequest pageRequest) {
        return search(CardRq.builder()
                .view(view)
                .searchText(searchText)
                .page(pageRequest.getPageNumber())
                .size(pageRequest.getPageSize())
                .sort(sortOrderMap(pageRequest.getSort()))
                .build()
        );
    }

    @Override
    public CardRs findAllByTextWithoutView(String searchText, PageRequest pageRequest) {
        return findAllByViewAndLikeTitleMap(null, searchText, pageRequest);
    }

    public CardRs findWithOrderByPoint(Integer viewType) {
        return search(CardRq.builder()
                .view(viewType)
                .sort(Map.of("point", CardRq.SortOrder.DESC))
                .build()
        );
    }

    public CardRs findByCardStatus(CardStatusDto cardStatus) {
        return search(CardRq.builder()
                .cardStatusId(cardStatus.getId())
                .build()
        );
    }

    public CardRs findByCardType(CardTypeDto cardType) {
        return search(CardRq.builder()
                .cardTypeId(cardType.getId())
                .build()
        );
    }

    public List<CardDto> findByTagId(UUID tagId) {
        return Collections.emptyList();
    }

    private CardRs search(CardRq rq) {
        String rqJson = toJson(rq);
        log.debug(">> Search card to LOCAL, rq: {}", rqJson);
        CardRs rs = CardRs.builder()
                .cards(findAll())
                .total(1L)
                .size(1)
                .page(1)
                .build();
        log.debug("<< Search card mapping rs: {}", rs);
        return rs;
    }

}
