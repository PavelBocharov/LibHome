package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByBeforeIdIsNull();
    List<Task> findByAfterIdIsNull();

}
