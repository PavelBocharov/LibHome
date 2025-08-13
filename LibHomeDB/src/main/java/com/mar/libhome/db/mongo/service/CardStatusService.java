package com.mar.libhome.db.mongo.service;

import com.mar.libhome.db.mongo.mapper.CardStatusMapper;
import com.mar.libhome.db.mongo.repo.CardStatusRepository;
import com.mar.libhome.dto.CardStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardStatusService {

    private final CardStatusRepository repository;
    private final CardStatusMapper mapper;

    public Mono<List<CardStatusDto>> getAll() {
        return Flux.fromIterable(repository.findAll())
                .map(mapper::toDto)
                .collectList();
    }

    public  Mono<CardStatusDto> deleteById(UUID id) {
        return Mono.justOrEmpty(id)
                .map(uuid -> repository.findById(uuid).orElseThrow(() -> new RuntimeException("Cannot find card status with id: " + uuid)))
                .map(mapper::toDto)
                .doOnSuccess(cardStatusDto -> repository.deleteById(cardStatusDto.getId()));
    }

    public Mono<CardStatusDto> save(CardStatusDto dto) {
        return Mono.just(dto)
                .map(mapper::toEntity)
                .map(repository::save)
                .map(mapper::toDto);
    }

}
