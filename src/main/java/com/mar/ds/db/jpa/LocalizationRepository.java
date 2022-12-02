package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.Localization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalizationRepository extends JpaRepository<Localization, Long> {
}
