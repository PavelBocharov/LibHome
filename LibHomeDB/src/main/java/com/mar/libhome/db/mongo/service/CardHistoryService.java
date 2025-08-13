package com.mar.libhome.db.mongo.service;

import com.mar.libhome.dto.CardHistoryDto;
import com.mar.libhome.db.mongo.mapper.CardHistoryMapper;
import com.mar.libhome.db.mongo.repo.CardHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardHistoryService {

    private final CardHistoryRepository repository;
    private final CardHistoryMapper mapper;

    public Mono<List<CardHistoryDto>> getAll() {
        return Flux.fromIterable(repository.findAll())
                .map(mapper::toDto)
                .collectList();
    }

    public Mono<CardHistoryDto> getById(UUID id) {
        return Mono.justOrEmpty(repository.findById(id))
                .map(mapper::toDto);
    }

    public Mono<List<CardHistoryDto>> save(List<CardHistoryDto> dto) {
        return Flux.fromIterable(dto)
                .map(mapper::toEntity)
                .collectList()
                .map(repository::saveAll)
                .flatMapIterable(cardHistories -> cardHistories)
                .map(mapper::toDto)
                .collectList();
    }

}
