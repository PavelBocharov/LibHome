package com.mar.libhome.db.mongo.repo;

import com.mar.libhome.db.mongo.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface CardRepository extends MongoRepository<Card, UUID> {

    Page<Card> findByViewType(Integer viewType, Pageable pageable);

    Page<Card> findByCardTypeId(UUID cardTypeId, Pageable pageable);

    Page<Card> findByCardStatusId(UUID cardStatusId, Pageable pageable);

}
