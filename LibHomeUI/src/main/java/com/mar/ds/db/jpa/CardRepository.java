package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.CardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * Репозиторий для работы с карточкой.
 */
@Deprecated
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query(value = "SELECT card FROM Card card WHERE card.cardStatus = :cardStatus or card.oldCardStatus = :cardStatus")
    List<Card> findByCardStatus(@NotNull CardStatus cardStatus);

    List<Card> findByCardType(@NotNull CardType cardType);

    @Query(value = "SELECT card FROM Card card INNER JOIN card.tagList tag WHERE tag.id = :id")
    List<Card> findByTagIn(@NotNull Long id);

    @Query(value = "SELECT card FROM Card card WHERE card.viewType = :view ORDER BY card.point DESC")
    List<Card> findWithOrderByPoint(@NotNull Integer view);

    @Query(value = "SELECT c FROM Card c JOIN c.cardStatus cs WHERE c.viewType = :view")
    Page<Card> findAllByView(@NotNull Integer view, Pageable pageable);

    @Query(value = """
            SELECT
                c
            FROM Card c
            JOIN c.cardStatus cs
            WHERE
                c.id in (
                    SELECT
                        DISTINCT(card.id)
                    FROM Card card
                    LEFT JOIN card.tagList tags
                    LEFT JOIN card.cardType types
                    WHERE
                        card.viewType = :view
                        AND (
                            lower(card.title) like lower(concat('%', :searchText,'%'))
                            OR lower(card.info) like lower(concat('%', :searchText,'%'))
                            OR lower(tags.title) like lower(concat('%', :searchText,'%'))
                            OR lower(types.title) like lower(concat('%', :searchText,'%'))
                        )
                )
            """)
    Page<Card> findAllByViewAndLikeTitleMap(@NotNull Integer view, String searchText, Pageable pageable);

}
