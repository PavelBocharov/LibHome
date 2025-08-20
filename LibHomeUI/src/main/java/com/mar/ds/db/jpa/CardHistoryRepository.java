package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.CardHistory;
import org.springframework.data.jpa.repository.JpaRepository;

@Deprecated(forRemoval = false)
public interface CardHistoryRepository extends JpaRepository<CardHistory, Long> {

}
