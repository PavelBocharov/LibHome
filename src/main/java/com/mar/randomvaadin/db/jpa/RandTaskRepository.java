package com.mar.randomvaadin.db.jpa;

import com.mar.randomvaadin.db.entity.RandTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface RandTaskRepository extends JpaRepository<RandTask, Long> {

    default Long create(@NonNull String text) {
        Integer number = Long.valueOf(this.count()).intValue() + 1;
        return this.save(new RandTask(number, text)).getId();
    }

}
