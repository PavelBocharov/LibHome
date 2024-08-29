package com.mar.ds.db.entity;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card_type_tag")
public class CardTypeTag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_type_tag_seq")
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @ManyToOne
    @JoinColumn(name = "card_type_id", nullable = false)
    private CardType cardType;

    @ManyToMany(mappedBy = "tagList")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Card> cardList;

    @Override
    public String toString() {
        return "CardTypeTag{" +
                "title='" + title + '\'' +
                ", id=" + id +
                '}';
    }
}
