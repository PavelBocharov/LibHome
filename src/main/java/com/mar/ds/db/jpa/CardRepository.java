package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import javax.validation.constraints.NotNull;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByCardStatus(@NotNull CardStatus cardStatus);

    List<Card> findByCardType(@NotNull CardType cardType);

    @Query(value = "SELECT card FROM Card card INNER JOIN card.tagList tag WHERE tag.id = :id")
    List<Card> findByTagIn(@NotNull Long id);

    @Query(value = "SELECT card FROM Card card ORDER BY card.point DESC")
    List<Card> findWithOrderByPoint();

}
