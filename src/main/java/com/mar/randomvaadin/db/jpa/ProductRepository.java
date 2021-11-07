package com.mar.randomvaadin.db.jpa;

import com.mar.randomvaadin.db.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
