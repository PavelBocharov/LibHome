package com.mar.randomvaadin.db.jpa;

import com.mar.randomvaadin.db.entity.RandTask;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RandTaskRepository extends JpaRepository<RandTask, Long> {

    default List<RandTask> findAllOrderById(Sort.Direction sort) {
        return this.findAll(Sort.by(sort, "id"));
    }

}
