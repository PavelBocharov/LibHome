package com.mar.libhome.db.repo;

import com.mar.libhome.db.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {

    Page<Card> findByViewType(Integer viewType, Pageable pageable);

    Page<Card> findByCardTypeId(UUID cardTypeId, Pageable pageable);

    Page<Card> findByCardStatusId(UUID cardStatusId, Pageable pageable);

    @Query(value = "SELECT card FROM Card card INNER JOIN card.tagList tag WHERE tag.id = :id")
    Page<Card> findByTagIn(@NotNull UUID id, Pageable pageable);

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
