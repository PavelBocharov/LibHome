package com.mar.ds.service;

import com.mar.ds.db.jpa.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final RandTaskRepository randTaskRepository;
    private final ProductRepository productRepository;
    private final ReceiptRepository receiptRepository;
    private final ItemRepository itemRepository;
    private final ItemStatusRepository itemStatusRepository;
    private final ItemTypeRepository itemTypeRepository;

}
