package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.Dialog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DialogRepository extends JpaRepository<Dialog, Long> {
}
