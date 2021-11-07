package com.mar.randomvaadin.db.jpa;

import com.mar.randomvaadin.db.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}
