package com.mar.ds.db.entity;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card")
public class Card implements Serializable, HasId {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_seq")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "point", nullable = true)
    private Double point;

    @Column(name = "info", nullable = false)
    private String info;

    @Column(name = "link", nullable = true)
    private String link;

    @Column(name = "last_game", nullable = false)
    private Date lastGame;

    @Column(name = "last_update", nullable = false)
    private Date lastUpdate;

    @OneToOne(optional = false)
    private CardType cardType;

    @OneToOne(optional = false)
    private CardStatus cardStatus;

    @ManyToMany
    @JoinTable(
            name = "card_tag_join",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<CardTypeTag> tagList;

}
