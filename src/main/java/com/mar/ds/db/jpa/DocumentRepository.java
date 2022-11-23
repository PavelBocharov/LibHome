package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {

}
