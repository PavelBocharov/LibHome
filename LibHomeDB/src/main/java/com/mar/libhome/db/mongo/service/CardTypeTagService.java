package com.mar.libhome.db.mongo.service;

import com.mar.libhome.db.mongo.mapper.CardTypeTagMapper;
import com.mar.libhome.db.mongo.repo.CardTypeRepository;
import com.mar.libhome.db.mongo.repo.CardTypeTagRepository;
import com.mar.libhome.dto.CardTypeTagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardTypeTagService {

    private final CardTypeTagRepository repository;
    private final CardTypeRepository cardTypeRepository;
    private final CardTypeTagMapper mapper;

    public Mono<List<CardTypeTagDto>> getAll() {
        return Flux.fromIterable(repository.findAll())
                .map(mapper::toDto)
                .collectList();
    }

    public Mono<CardTypeTagDto> deleteById(UUID id) {
        return Mono.justOrEmpty(id)
                .map(uuid -> repository
                        .findById(uuid)
                        .orElseThrow(() -> new RuntimeException("Cannot find card type tag with id: " + uuid))
                )
                .map(mapper::toDto)
                .doOnSuccess(cardTypeDto -> repository.deleteById(cardTypeDto.getId()));
    }

    public Mono<List<CardTypeTagDto>> save(List<CardTypeTagDto> dto) {
        return Flux.fromIterable(dto)
                .map(tag -> {
                    if (tag.getCardTypeId() == null) {
                        throw new RuntimeException("Cannot find card type for create tag = id is NULL. Dto: " + dto);
                    }
                    cardTypeRepository.findById(tag.getCardTypeId())
                            .orElseThrow(
                                    () -> new RuntimeException("Cannot find card type for create tag. Dto: " + dto)
                            );
                    return tag;
                })
                .map(mapper::toEntity)
                .collectList()
                .map(repository::saveAll)
                .flatMapIterable(cardTypeTags -> cardTypeTags)
                .map(mapper::toDto)
                .collectList();
    }

    public Mono<List<CardTypeTagDto>> findAllByCardTypeId(UUID cardTypeId) {
        return Flux.fromIterable(repository.findAllByCardTypeId(cardTypeId))
                .map(mapper::toDto)
                .collectList();
    }

}
