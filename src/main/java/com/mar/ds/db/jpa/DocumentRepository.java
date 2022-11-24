package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByDialogIdIsNull();

}
