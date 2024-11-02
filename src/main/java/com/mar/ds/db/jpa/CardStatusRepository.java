package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CardStatusRepository extends JpaRepository<CardStatus, Long> {

    @Query(value = "select nextval('card_status_order')", nativeQuery = true)
    public Long getNextSortOrderNumber();

}
