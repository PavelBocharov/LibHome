package com.mar.libhome.db.repo;


import com.mar.libhome.db.entity.CardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardTypeRepository extends JpaRepository<CardType, UUID> {
}
