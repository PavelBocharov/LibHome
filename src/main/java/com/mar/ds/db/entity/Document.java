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
@Table(name = "document")
public class Document implements Serializable, HasId {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_seq")
    private Long id;

    @Column(name = "btn_title", nullable = false)
    private String btnTitle;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "image", nullable = true)
    private String image;

    @Column(name = "text", nullable = false)
    private String text;

    @OneToOne(optional = false)
    private DocumentStatus documentStatus;

    @OneToOne
    private DocumentType documentType;

    @ManyToOne
    @JoinColumn(name="dialog_id", nullable=true)
    private Dialog dialog;
}
