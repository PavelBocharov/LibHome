package com.mar.ds.db.entity;

import com.mar.ds.views._build.popup.PopupEntity;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card_type")
public class CardType implements Serializable, HasId, PopupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_type_seq")
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @OneToMany(mappedBy = "id")
    @LazyCollection(LazyCollectionOption.FALSE)
    List<CardTypeTag> tags;

}
