package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.CardHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardHistoryRepository extends JpaRepository<CardHistory, Long> {

    List<CardHistory> findAllByEditableId(Long editableId);

}
