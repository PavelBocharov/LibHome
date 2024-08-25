package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

}
