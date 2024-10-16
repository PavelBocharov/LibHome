package com.mar.ds.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_seq_name")
    @SequenceGenerator(name = "card_seq_name", sequenceName = "card_seq", allocationSize = 1)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "point")
    private Double point;

    @Column(name = "info")
    private String info;

    @Column(name = "link")
    private String link;

    @Column(name = "last_game")
    private Date lastGame;

    @Column(name = "last_update")
    private Date lastUpdate;

    @Column(name = "engine")
    private GameEngine engine;

    @Column(name = "language")
    private Language language = Language.DEFAULT;

    @Column(name = "view_type", nullable = false)
    @ColumnDefault("1")
    private ViewType viewType;

    @OneToOne
    private CardType cardType;

    @OneToOne
    private CardStatus cardStatus;

    @ManyToMany
    @JoinTable(
            name = "card_tag_join",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<CardTypeTag> tagList;

}
