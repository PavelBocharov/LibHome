package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemStatusRepository extends JpaRepository<ItemStatus, Long> {
}
