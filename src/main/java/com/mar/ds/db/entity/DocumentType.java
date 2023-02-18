package com.mar.ds.db.entity;

import com.mar.ds.views._build.popup.PopupEntity;
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
@Table(name = "document_type")
public class DocumentType implements Serializable, HasId, PopupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_type_seq")
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

}
