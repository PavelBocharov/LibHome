package com.mar.libhome.db.service;

import com.mar.libhome.db.entity.CardStatus;
import com.mar.libhome.db.mapper.CardStatusMapper;
import com.mar.libhome.db.repo.CardStatusRepository;
import com.mar.libhome.dto.CardStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardStatusService {

    private final CardStatusRepository repository;
    private final CardStatusMapper mapper;

    public List<CardStatusDto> getAll() {
        return repository.findAll()
                .parallelStream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public CardStatusDto deleteById(UUID id) {
        CardStatus status = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find card status with id: " + id));
        repository.delete(status);
        return mapper.toDto(status);
    }

    @Transactional
    public List<CardStatusDto> saveAll(List<CardStatusDto> dtos) {
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

    public CardStatusDto findByTechId(String techId) {
        return mapper.toDto(repository.findByTech(techId));
    }

    public List<CardStatusDto> findAllWithTechIsNull() {
        return repository.findByTechIsNull()
                .parallelStream()
                .map(mapper::toDto)
                .toList();
    }

}
