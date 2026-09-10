package com.mar.ds.db.remote.local;

import com.mar.ds.db.remote.CardStatusRemote;
import com.mar.libhome.dto.CardStatusDto;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.mar.libhome.utils.RestApiUtils.toJson;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@Profile("!production")
public class CardStatusRemoteImpl implements CardStatusRemote {

    public static final CardStatusDto data = CardStatusDto.builder()
            .id(UUID.randomUUID())
            .title("TEST STATUS")
            .icon(VaadinIcon.SEARCH.name())
            .build();

    public List<CardStatusDto> findAll() {
        log.debug(">> Find all card status - LOCAL");
        List<CardStatusDto> rs = List.of(data);
        log.debug("<< Find all card status. List mapping rs: {}", rs);
        return rs;
    }

    public List<CardStatusDto> saveAll(List<CardStatusDto> cardStatusList) {
        String rqJson = toJson(cardStatusList);
        log.debug(">> Save all card status list to uri: LOCAL, rq: {}", rqJson);
        List<CardStatusDto> rs = findAll();
        log.debug("<< Save all card status list mapping rs: {}", rs);
        return rs;
    }

    public CardStatusDto save(CardStatusDto diff) {
        return saveAll(Collections.singletonList(diff)).get(0);
    }

    public CardStatusDto remove(CardStatusDto dto) {
        String rqJson = toJson(dto);
        log.debug(">> Delete card status to uri: LOCAL, rq: {}", rqJson);
        return data;
    }

    public CardStatusDto findByTechId(String techId) {
        if (isBlank(techId)) {
            return null;
        }
        log.debug(">> Find card status by tech id: {} - LOCAL", techId);
        CardStatusDto rs = findAll().get(0);
        log.debug("<< Find card status by tech id: {}. List mapping rs: {}", techId, rs);
        return rs;
    }

    public List<CardStatusDto> findByTechIdIsNull() {
        log.debug(">> Find card status by tech id is null - LOCAL");
        List<CardStatusDto> rs = findAll();
        log.debug("<< Find card status by tech id is null. List mapping rs: {}", rs);
        return rs;
    }

}
