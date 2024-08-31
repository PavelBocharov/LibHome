package com.mar.ds.service;

import com.mar.ds.db.jpa.CardRepository;
import com.mar.ds.db.jpa.CardStatusRepository;
import com.mar.ds.db.jpa.CardTypeRepository;
import com.mar.ds.db.jpa.CardTypeTagRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final CardRepository cardRepository;
    private final CardTypeRepository cardTypeRepository;
    private final CardStatusRepository cardStatusRepository;
    private final CardTypeTagRepository cardTypeTagRepository;

}
