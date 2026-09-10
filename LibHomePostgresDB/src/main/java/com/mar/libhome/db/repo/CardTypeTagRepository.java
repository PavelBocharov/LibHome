package com.mar.libhome.db.repo;


import com.mar.libhome.db.entity.CardTypeTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CardTypeTagRepository extends JpaRepository<CardTypeTag, UUID> {

    List<CardTypeTag> findAllByCardTypeId(UUID cardTypeId);

}
