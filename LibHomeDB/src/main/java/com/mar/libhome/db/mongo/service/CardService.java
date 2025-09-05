package com.mar.libhome.db.mongo.service;

import com.mar.libhome.db.mongo.entity.Card;
import com.mar.libhome.db.mongo.entity.CardTypeTag;
import com.mar.libhome.db.mongo.mapper.CardMapper;
import com.mar.libhome.db.mongo.mapper.CardStatusMapper;
import com.mar.libhome.db.mongo.mapper.CardTypeMapper;
import com.mar.libhome.db.mongo.mapper.CardTypeTagMapper;
import com.mar.libhome.db.mongo.repo.CardRepository;
import com.mar.libhome.db.mongo.repo.CardStatusRepository;
import com.mar.libhome.db.mongo.repo.CardTypeRepository;
import com.mar.libhome.db.mongo.repo.CardTypeTagRepository;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardRq;
import com.mar.libhome.dto.CardTypeTagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

    public Mono<List<CardDto>> getAll() {
        return Flux.fromIterable(repository.findAll())
                .map(mapper::toDto)
                .collectList();
    }

    public Mono<List<CardDto>> search(CardRq rq) {
        return Flux.fromIterable(searchCards(rq))
                .map(mapper::toDto)
                .collectList();
    }

    private List<Card> searchCards(CardRq rq) {
        PageRequest pageRequest = getPageRequest(rq);
        if (rq.getView() != null) {
            if (rq.getSearchText() != null) {
                return repository.findByText(rq.getView(), rq.getSearchText(), pageRequest).getContent();
            }
            return repository.findByViewType(rq.getView(), pageRequest).getContent();
        }
        if (rq.getCardTypeId() != null) {
            return repository.findByCardTypeId(rq.getCardTypeId(), pageRequest).getContent();
        }
        if (rq.getCardStatusId() != null) {
            return repository.findByCardStatusId(rq.getCardStatusId(), pageRequest).getContent();
        }
//        if (rq.getCardTagId() != null) {
//
//        }
        return repository.findAll(pageRequest).getContent();
    }

    public Mono<List<CardDto>> save(List<CardDto> dto) {
        return Flux.fromIterable(dto)
                .map(mapper::toEntity)
                .collectList()
                .map(repository::saveAll)
                .flatMapIterable(cardHistories -> cardHistories)
                .map(mapper::toDto)
                .collectList();
    }

    public Mono<CardDto> deleteById(UUID id) {
        return Mono.justOrEmpty(id)
                .map(uuid -> repository.findById(uuid).orElseThrow(() -> new RuntimeException("Cannot find card with id: " + uuid)))
                .map(mapper::toDto)
                .doOnSuccess(cardDto -> repository.deleteById(cardDto.getId()));

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
            return null;
        }
        int page = Optional.ofNullable(rq.getPage()).orElse(0);
        int size = Optional.ofNullable(rq.getSize()).orElse(Integer.MAX_VALUE);
        if (rq.getSort() == null) {
            return PageRequest.of(page, size);
        } else {
            List<Sort.Order> orders = new ArrayList<>(rq.getSort().size());
            for (String field : rq.getSort().keySet()) {
                if (CardRq.SortOrder.ASC.equals(rq.getSort().get(field))) {
                    orders.add(Sort.Order.asc(field));
                } else {
                    orders.add(Sort.Order.desc(field));
                }
            }
            return PageRequest.of(page, size, Sort.by(orders));
        }
    }

}
