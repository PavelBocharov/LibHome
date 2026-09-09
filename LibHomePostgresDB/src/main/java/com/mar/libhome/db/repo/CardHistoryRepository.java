package com.mar.libhome.db.repo;

import com.mar.libhome.db.entity.CardHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CardHistoryRepository extends JpaRepository<CardHistory, UUID> {

    List<CardHistory> findAllByEditableId(UUID editableId);

}
