package com.mar.ds.db.remote;

import com.mar.libhome.api.CardApi;
import com.mar.libhome.api.data.PageRequest;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardRq;
import com.mar.libhome.dto.CardRs;
import com.mar.libhome.dto.CardStatusDto;
import com.mar.libhome.dto.CardTypeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Profile("production")
@RequiredArgsConstructor
public class CardRemoteImpl implements CardRemote {

    private final CardApi cardApi;

    public List<CardDto> findAll() {
        return cardApi.getAllCards();
    }

    public CardRs findAll(PageRequest pageRequest) {
        return cardApi.searchCard(CardRq.builder()
                .page(pageRequest.getPageNumber())
                .size(pageRequest.getPageSize())
                .sort(sortOrderMap(pageRequest.getSort()))
                .build()
        );
    }

    public List<CardDto> saveAll(List<CardDto> dtoList) {
        return cardApi.saveCards(dtoList);
    }

    public CardDto save(CardDto dto) {
        return saveAll(Collections.singletonList(dto)).get(0);
    }

    public CardDto remove(CardDto dto) {
        return cardApi.deleteCard(dto);
    }

    private Map<String, CardRq.SortOrder> sortOrderMap(PageRequest.Sort sort) {
        if (sort == null) {
            return Collections.emptyMap();
        }
        Map<String, CardRq.SortOrder> map = new LinkedHashMap<>();

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
        return cardApi.searchCard(CardRq.builder()
                .view(view)
                .page(pageRequest.getPageNumber())
                .size(pageRequest.getPageSize())
                .sort(sortOrderMap(pageRequest.getSort()))
                .build()
        );
    }

    public CardRs findAllByViewAndLikeTitleMap(Integer view, String searchText, PageRequest pageRequest) {
        return cardApi.searchCard(CardRq.builder()
                .view(view)
                .searchText(searchText)
                .page(pageRequest.getPageNumber())
                .size(pageRequest.getPageSize())
                .sort(sortOrderMap(pageRequest.getSort()))
                .build()
        );
    }

    public CardRs findAllByTextWithoutView(String searchText, PageRequest pageRequest) {
        CardRq rq = CardRq.builder()
                .view(null)
                .searchText(searchText)
                .page(pageRequest.getPageNumber())
                .size(pageRequest.getPageSize())
                .sort(sortOrderMap(pageRequest.getSort()))
                .build();

        return cardApi.searchCardWithoutViewByText(rq);
    }

    public CardRs findWithOrderByPoint(Integer viewType) {
        return cardApi.searchCard(CardRq.builder()
                .view(viewType)
                .sort(Map.of("point", CardRq.SortOrder.DESC))
                .build()
        );
    }

    public CardRs findByCardStatus(CardStatusDto cardStatus) {
        return cardApi.searchCard(CardRq.builder()
                .cardStatusId(cardStatus.getId())
                .build()
        );
    }

    public CardRs findByCardType(CardTypeDto cardType) {
        return cardApi.searchCard(CardRq.builder()
                .cardTypeId(cardType.getId())
                .build()
        );
    }

    public List<CardDto> findByTagId(UUID tagId) {
        return Collections.emptyList();
    }

}
