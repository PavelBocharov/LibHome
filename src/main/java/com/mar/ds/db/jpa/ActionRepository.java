package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionRepository extends JpaRepository<Action, Long> {
}
