package com.mar.libhome.db.mongo.service;

import com.mar.libhome.db.mongo.mapper.CardHistoryMapper;
import com.mar.libhome.db.mongo.repo.CardHistoryRepository;
import com.mar.libhome.dto.CardHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardHistoryService {

    private final CardHistoryRepository repository;
    private final CardHistoryMapper mapper;

    public List<CardHistoryDto> getAll() {
        return repository.findAll()
                .parallelStream()
                .map(mapper::toDto)
                .toList();
    }

    public List<CardHistoryDto> getByCardId(UUID id) {
        return repository.findAllByEditableId(id)
                .parallelStream()
                .map(mapper::toDto)
                .toList();
    }

    public List<CardHistoryDto> save(List<CardHistoryDto> dtos) {
        return repository.saveAll(
                        dtos.parallelStream()
                                .map(mapper::toEntity)
                                .toList()
                )
                .parallelStream()
                .map(mapper::toDto)
                .toList();
    }

}
