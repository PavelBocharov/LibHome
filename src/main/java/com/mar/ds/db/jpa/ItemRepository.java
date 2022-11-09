package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
