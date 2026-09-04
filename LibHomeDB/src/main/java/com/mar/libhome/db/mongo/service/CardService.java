package com.mar.libhome.db.mongo.service;

import com.mar.libhome.db.mongo.entity.Card;
import com.mar.libhome.db.mongo.entity.CardTypeTag;
import com.mar.libhome.db.mongo.entity.CountResult;
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
import com.mar.libhome.dto.CardRs;
import com.mar.libhome.dto.CardTypeTagDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
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

    private final MongoTemplate mongoTemplate;

    public List<CardDto> getAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .parallelStream()
                .map(mapper::toDto)
                .toList();
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
//        if (rq.getCardTagId() != null) {
//          TODO
//        }
        return repository.findAll(pageRequest);
    }

    private Page<Card> searchByText(CardRq rq) {
        PageRequest pageRequest = getPageRequest(rq);

        MatchOperation preMatch = Aggregation.match(Criteria.where("view_type").is(rq.getView()));
        LookupOperation cardTypeLookup = Aggregation.lookup("card_type", "card_type_id", "_id", "type");
        LookupOperation cardTypeTagLookup = Aggregation.lookup("card_type_tag", "tag_id_list", "_id", "tag");

        MatchOperation lookupMatch = Aggregation.match(
                new Criteria().orOperator(
                        Criteria.where("title").regex(rq.getSearchText(), "i"),
                        Criteria.where("info").regex(rq.getSearchText(), "i"),
                        Criteria.where("tag.title").regex(rq.getSearchText(), "i"),
                        Criteria.where("type.title").regex(rq.getSearchText(), "i")
                )
        );

        Aggregation dataPip =
                Sort.unsorted().equals(pageRequest.getSort())
                        ?
                        Aggregation.newAggregation(
                                preMatch,
                                cardTypeLookup,
                                cardTypeTagLookup,
                                lookupMatch,
                                Aggregation.skip((long) rq.getPage() * rq.getSize()),
                                Aggregation.limit(rq.getSize()))
                        :
                        Aggregation.newAggregation(
                                preMatch,
                                Aggregation.sort(pageRequest.getSort()),
                                cardTypeLookup,
                                cardTypeTagLookup,
                                lookupMatch,
                                Aggregation.skip((long) rq.getPage() * rq.getSize()),
                                Aggregation.limit(rq.getSize()));

        Aggregation countPip = Aggregation.newAggregation(
                preMatch, cardTypeLookup, cardTypeTagLookup, lookupMatch,
                Aggregation.count().as("totalCount")
        );

        // TODO как-то упаковать в facet и получить все одним запросом?
        AggregationResults<Card> res = mongoTemplate.aggregate(dataPip, "card", Card.class);
        log.info("Get card by text: {}", res.getMappedResults());
        AggregationResults<CountResult> totalCount = mongoTemplate.aggregate(countPip, "card", CountResult.class);
        log.info("Get count by text: {}", totalCount.getRawResults());
        long count = totalCount.getUniqueMappedResult() == null ? 0 : totalCount.getUniqueMappedResult().getTotalCount();
        return new PageImpl<Card>(res.getMappedResults(), pageRequest, count);
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

    @Transactional
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
            orders.add(Sort.Order.asc("id"));
            return PageRequest.of(page, size, Sort.by(orders));
        }
    }

}
