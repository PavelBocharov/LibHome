package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {


    List<Mission> findByStartTaskIsNotNull();
    List<Mission> findByStartTaskIsNull();

}
