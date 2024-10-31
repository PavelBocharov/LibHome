package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.CardType;
import com.mar.ds.db.entity.ViewType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import javax.validation.constraints.NotNull;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByCardStatus(@NotNull CardStatus cardStatus);

    List<Card> findByCardType(@NotNull CardType cardType);

    @Query(value = "SELECT card FROM Card card INNER JOIN card.tagList tag WHERE tag.id = :id")
    List<Card> findByTagIn(@NotNull Long id);

    @Query(value = "SELECT card FROM Card card WHERE card.viewType = :view ORDER BY card.point DESC")
    List<Card> findWithOrderByPoint(@NotNull ViewType view);

    @Query(value = "SELECT card FROM Card card WHERE card.viewType = :view")
    Page<Card> findAllByView(@NotNull ViewType view, Pageable pageable);

    @Query(value = """
            SELECT 
                c
            FROM Card c 
            WHERE
                c.id in (
                    SELECT
                        DISTINCT(card.id)
                    FROM Card card
                    JOIN card.tagList tags
                    WHERE
                        card.viewType = :view
                        and (
                            lower(card.title) like lower(concat('%', :searchText,'%'))
                            or lower(card.info) like lower(concat('%', :searchText,'%'))
                            or lower(tags.title) like lower(concat('%', :searchText,'%'))
                        )
                )
            """)
    Page<Card> findAllByViewAndLikeTitle(@NotNull ViewType view, String searchText, Pageable pageable);

}
