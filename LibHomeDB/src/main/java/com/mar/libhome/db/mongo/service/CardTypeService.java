package com.mar.libhome.db.mongo.service;

import com.mar.libhome.db.mongo.entity.CardType;
import com.mar.libhome.db.mongo.mapper.CardTypeMapper;
import com.mar.libhome.db.mongo.repo.CardTypeRepository;
import com.mar.libhome.dto.CardTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardTypeService {

    private final CardTypeRepository repository;
    private final CardTypeMapper mapper;

    public List<CardTypeDto> getAll() {
        return repository.findAll()
                .parallelStream()
                .map(mapper::toDto)
                .toList();
    }

    public CardTypeDto deleteById(UUID id) {
        CardType type = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find card type with id: " + id));
        repository.delete(type);
        return mapper.toDto(type);
    }

    public List<CardTypeDto> save(List<CardTypeDto> dtos) {
        return repository.saveAll(
                        dtos
                                .parallelStream()
                                .map(mapper::toEntity)
                                .toList()
                )
                .parallelStream()
                .map(mapper::toDto)
                .toList();
    }

}
