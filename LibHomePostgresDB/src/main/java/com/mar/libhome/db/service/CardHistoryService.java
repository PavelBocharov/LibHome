package com.mar.libhome.db.service;

import com.mar.libhome.db.entity.CardHistory;
import com.mar.libhome.db.mapper.CardHistoryMapper;
import com.mar.libhome.db.repo.CardHistoryRepository;
import com.mar.libhome.dto.CardHistoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
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

    @Transactional
    public List<CardHistoryDto> save(List<CardHistoryDto> dtos) {
        List<CardHistory> historyList = dtos.parallelStream()
                .map(mapper::toEntity)
                .toList();
        log.info("PRE save card history list: {}", historyList);
        return repository.saveAllAndFlush(historyList)
                .parallelStream()
                .peek(System.out::println)
                .map(mapper::toDto)
                .peek(System.out::println)
                .toList();
    }

}
