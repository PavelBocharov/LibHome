package com.mar.ds.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dialog")
public class Dialog implements Serializable, HasId {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dialog_seq")
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @OneToOne(optional = false)
    private Character character;

    @OneToMany(mappedBy = "dialog")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Item> items;

    @OneToMany(mappedBy = "dialog")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Document> documents;

    @OneToMany(mappedBy = "dialog")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Action> actions;

}
