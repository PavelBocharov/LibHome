package com.mar.ds.service;

import com.mar.ds.db.jpa.CardTypeRepository;
import com.mar.ds.db.jpa.CardTypeTagRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final CardTypeRepository cardTypeRepository;
    private final CardTypeTagRepository cardTypeTagRepository;

}
