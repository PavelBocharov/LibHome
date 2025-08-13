package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.CardHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий работы с таблицей изменений карточки.
 */
public interface CardHistoryRepository extends JpaRepository<CardHistory, Long> {

    List<CardHistory> findAllByEditableId(Long editableId);

    List<CardHistory> deleteByColumnName(String columnName);

}
