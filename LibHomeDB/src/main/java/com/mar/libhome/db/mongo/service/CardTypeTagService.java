package com.mar.libhome.db.mongo.service;

import com.mar.libhome.db.mongo.entity.CardTypeTag;
import com.mar.libhome.db.mongo.mapper.CardTypeTagMapper;
import com.mar.libhome.db.mongo.repo.CardTypeRepository;
import com.mar.libhome.db.mongo.repo.CardTypeTagRepository;
import com.mar.libhome.dto.CardTypeTagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardTypeTagService {

    private final CardTypeTagRepository repository;
    private final CardTypeRepository cardTypeRepository;
    private final CardTypeTagMapper mapper;

    public List<CardTypeTagDto> getAll() {
        return repository.findAll()
                .parallelStream()
                .map(mapper::toDto)
                .toList();
    }

    public CardTypeTagDto deleteById(UUID id) {
        CardTypeTag tag = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find card type tag with id: " + id));
        repository.delete(tag);
        return mapper.toDto(tag);
    }

    public List<CardTypeTagDto> save(List<CardTypeTagDto> dtos) {
        return repository.saveAll(
                        dtos
                                .parallelStream()
                                .peek(tag -> {
                                    if (tag.getCardTypeId() == null) {
                                        throw new RuntimeException("Cannot find card type for create tag = id is NULL. Dto: " + dtos);
                                    }
                                    cardTypeRepository.findById(tag.getCardTypeId())
                                            .orElseThrow(
                                                    () -> new RuntimeException("Cannot find card type for create tag. Dto: " + dtos)
                                            );
                                })
                                .map(mapper::toEntity)
                                .toList()
                )
                .parallelStream()
                .map(mapper::toDto)
                .toList();
    }

    public List<CardTypeTagDto> findAllByCardTypeId(UUID cardTypeId) {
        return repository.findAllByCardTypeId(cardTypeId)
                .parallelStream()
                .map(mapper::toDto)
                .toList();
    }

}
