package com.mar.ds.service;

import com.mar.ds.db.jpa.*;
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

}
