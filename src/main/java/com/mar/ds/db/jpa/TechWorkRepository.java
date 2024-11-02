package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.TechWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TechWorkRepository extends JpaRepository<TechWork, Long> {

    @Query("SELECT MAX(tw.techId) FROM TechWork tw")
    Optional<Long> findWithMaxTechId();

}
