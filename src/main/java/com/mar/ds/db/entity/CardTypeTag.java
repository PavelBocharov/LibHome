package com.mar.ds.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card_type_tag")
public class CardTypeTag implements HasId, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_type_tag_seq")
    private Long id;

    @Column(name = "title", nullable = false)
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
