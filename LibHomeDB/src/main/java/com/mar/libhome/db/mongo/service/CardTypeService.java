package com.mar.libhome.db.mongo.service;

import com.mar.libhome.db.mongo.mapper.CardTypeMapper;
import com.mar.libhome.db.mongo.repo.CardTypeRepository;
import com.mar.libhome.dto.CardTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardTypeService {

    private final CardTypeRepository repository;
    private final CardTypeMapper mapper;

    public Mono<List<CardTypeDto>> getAll() {
        return Flux.fromIterable(repository.findAll())
                .map(mapper::toDto)
                .collectList();
    }

    public Mono<CardTypeDto> deleteById(UUID id) {
        return Mono.justOrEmpty(id)
                .map(uuid -> repository.findById(uuid).orElseThrow(() -> new RuntimeException("Cannot find card type with id: " + uuid)))
                .map(mapper::toDto)
                .doOnSuccess(cardTypeDto -> repository.deleteById(cardTypeDto.getId()));
    }

    public Mono<List<CardTypeDto>> save(List<CardTypeDto> dtoList) {
        return Flux.fromIterable(dtoList)
                .map(mapper::toEntity)
                .collectList()
                .map(repository::saveAll)
                .flatMapIterable(cardTypes -> cardTypes)
                .map(mapper::toDto)
                .collectList();
    }

}
