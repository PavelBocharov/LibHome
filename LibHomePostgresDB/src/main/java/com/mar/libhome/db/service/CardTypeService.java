package com.mar.libhome.db.service;

import com.mar.libhome.db.entity.CardType;
import com.mar.libhome.db.mapper.CardTypeMapper;
import com.mar.libhome.db.repo.CardTypeRepository;
import com.mar.libhome.dto.CardTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public CardTypeDto deleteById(UUID id) {
        CardType type = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find card type with id: " + id));
        repository.delete(type);
        return mapper.toDto(type);
    }

    @Transactional
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
