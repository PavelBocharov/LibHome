package com.mar.libhome.db.mongo.repo;

import com.mar.libhome.db.mongo.entity.CardType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface CardTypeRepository extends MongoRepository<CardType, UUID> {
}
