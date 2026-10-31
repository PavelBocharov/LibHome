package com.mar.libhome.db.repo;

import com.mar.libhome.db.entity.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CardStatusRepository extends JpaRepository<CardStatus, UUID> {

    CardStatus findByTech(String tech);

    List<CardStatus> findByTechIsNull();

}
