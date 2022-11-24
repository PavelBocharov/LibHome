package com.mar.ds.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "document_status")
public class DocumentStatus implements Serializable, HasId {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_status_seq")
    private Long id;

    @Column(name = "enum_id", nullable = false, unique = true)
    private Long enumId;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

}
