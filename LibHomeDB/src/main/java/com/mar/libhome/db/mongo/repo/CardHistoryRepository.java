package com.mar.libhome.db.mongo.repo;

import com.mar.libhome.db.mongo.entity.CardHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface CardHistoryRepository extends MongoRepository<CardHistory, UUID> {
}
