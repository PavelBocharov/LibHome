package com.mar.randomvaadin.service;

import com.mar.randomvaadin.db.jpa.ProductRepository;
import com.mar.randomvaadin.db.jpa.RandTaskRepository;
import com.mar.randomvaadin.db.jpa.ReceiptRepository;
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

}
