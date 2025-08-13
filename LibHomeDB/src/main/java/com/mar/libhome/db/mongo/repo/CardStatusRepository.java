package com.mar.libhome.db.mongo.repo;

import com.mar.libhome.db.mongo.entity.CardStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface CardStatusRepository extends MongoRepository<CardStatus, UUID> {
}
