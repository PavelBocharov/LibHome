package com.mar.libhome.db.service;

import com.mar.libhome.db.entity.Card;
import com.mar.libhome.db.entity.CardTypeTag;
import com.mar.libhome.db.mapper.CardMapper;
import com.mar.libhome.db.mapper.CardStatusMapper;
import com.mar.libhome.db.mapper.CardTypeMapper;
import com.mar.libhome.db.mapper.CardTypeTagMapper;
import com.mar.libhome.db.repo.CardRepository;
import com.mar.libhome.db.repo.CardStatusRepository;
import com.mar.libhome.db.repo.CardTypeRepository;
import com.mar.libhome.db.repo.CardTypeTagRepository;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardRq;
import com.mar.libhome.dto.CardRs;
import com.mar.libhome.dto.CardTypeTagDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository repository;
    private final CardStatusRepository statusRepository;
    private final CardTypeRepository typeRepository;
    private final CardTypeTagRepository tagRepository;
    private final CardMapper mapper;
    private final CardStatusMapper statusMapper;
    private final CardTypeMapper typeMapper;
    private final CardTypeTagMapper tagMapper;

    public List<CardDto> getAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .parallelStream()
                .map(mapper::toDto)
                .toList();
    }

    public CardRs searchCardByTextWithoutView(CardRq rq) {
        PageRequest pageRequest = getPageRequest(rq);
        Page<Card> page = repository.findAllByTextWithoutView(rq.getSearchText(), pageRequest);
        return CardRs.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .total(page.getTotalElements())
                .cards(page.stream().parallel().map(mapper::toDto).map(this::enrich).toList())
                .build();
    }

    public CardRs search(CardRq rq) {
        Page<Card> page = searchCards(rq);
        return CardRs.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .total(page.getTotalElements())
                .cards(page.stream().parallel().map(mapper::toDto).map(this::enrich).toList())
                .build();
    }

    private Page<Card> searchCards(CardRq rq) {
        PageRequest pageRequest = getPageRequest(rq);
        if (rq.getView() != null) {
            if (rq.getSearchText() != null) {
                log.debug("Search by text. RQ: {}", rq);
                return searchByText(rq);
            }
            log.debug("Search by view type. RQ: {}", rq);
            return repository.findByViewType(rq.getView(), pageRequest);
        }
        if (rq.getCardTypeId() != null) {
            log.debug("Search by card type. RQ: {}", rq);
            return repository.findByCardTypeId(rq.getCardTypeId(), pageRequest);
        }
        if (rq.getCardStatusId() != null) {
            log.debug("Search by card status. RQ: {}", rq);
            return repository.findByCardStatusId(rq.getCardStatusId(), pageRequest);
        }
        if (rq.getCardTagId() != null) {
            log.debug("Search by card tag. RQ: {}", rq);
            return repository.findByTagIn(rq.getCardTagId(), pageRequest);
        }
        return repository.findAll(pageRequest);
    }

    private Page<Card> searchByText(CardRq rq) {
        PageRequest pageRequest = getPageRequest(rq);
        return repository.findAllByViewAndLikeTitleMap(rq.getView(), rq.getSearchText(), pageRequest);
    }

    @Transactional
    public List<CardDto> save(List<CardDto> dtos) {
        repository.saveAll(dtos.parallelStream().map(mapper::toEntity).toList());
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .parallelStream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public CardDto deleteById(UUID id) {
        Card card = repository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find card with id: " + id));
        repository.delete(card);
        return mapper.toDto(card);
    }

    public CardDto enrich(CardDto card) {
        card.setCardStatus(statusMapper.toDto(
                statusRepository.findById(card.getCardStatus().getId()).orElseThrow()
        ));
        if (card.getOldCardStatus() != null) {
            card.setOldCardStatus(statusMapper.toDto(
                    statusRepository.findById(card.getOldCardStatus().getId()).orElse(null)
            ));
        }
        card.setCardType(typeMapper.toDto(
                typeRepository.findById(card.getCardType().getId()).orElseThrow()
        ));

        if (card.getTagList() != null && !card.getTagList().isEmpty()) {
            List<CardTypeTagDto> tags = new ArrayList<>(card.getTagList().size());
            for (CardTypeTag tag : tagRepository.findAllById(card.getTagList().stream().map(CardTypeTagDto::getId).toList())) {
                tags.add(tagMapper.toDto(tag));
            }
            card.setTagList(tags);
        }

        return card;
    }


    private PageRequest getPageRequest(CardRq rq) {
        if (rq == null) {
            return PageRequest.of(0, Integer.MAX_VALUE, Sort.unsorted());
        }
        int page = Optional.ofNullable(rq.getPage()).orElse(0);
        int size = Optional.ofNullable(rq.getSize()).orElse(Integer.MAX_VALUE);
        if (rq.getSort() == null) {
            return PageRequest.of(page, size, Sort.unsorted());
        } else {
            List<Sort.Order> orders = new ArrayList<>(rq.getSort().size());
            for (String field : rq.getSort().keySet()) {
                if (CardRq.SortOrder.ASC.equals(rq.getSort().get(field))) {
                    orders.add(Sort.Order.asc(field));
                } else {
                    orders.add(Sort.Order.desc(field));
                }
            }
            orders.add(Sort.Order.asc("id"));
            return PageRequest.of(page, size, Sort.by(orders));
        }
    }

}
