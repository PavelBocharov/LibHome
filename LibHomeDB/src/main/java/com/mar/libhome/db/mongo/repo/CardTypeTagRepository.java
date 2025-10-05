package com.mar.libhome.db.mongo.repo;

import com.mar.libhome.db.mongo.entity.CardTypeTag;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface CardTypeTagRepository extends MongoRepository<CardTypeTag, UUID> {

    List<CardTypeTag> findAllByCardTypeId(UUID cardTypeId);

}
