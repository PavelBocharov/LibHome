package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.CardType;
import com.mar.ds.db.entity.CardTypeTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardTypeTagRepository extends JpaRepository<CardTypeTag, Long> {

    List<CardTypeTag> findByCardType(CardType cardType);

}
