package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CardStatusRepository extends JpaRepository<CardStatus, Long> {

    @Query(value = "SELECT cs FROM CardStatus cs WHERE cs.tech = :techId")
    CardStatus findByTechId(String techId);

    @Query(value = "SELECT cs FROM CardStatus cs WHERE cs.tech is NULL")
    List<CardStatus> findByWithTechIdIsNull();

}
