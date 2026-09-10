package com.mar.libhome.db.entity;

import com.mar.libhome.enums.GameEngine;
import com.mar.libhome.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "point")
    private Double point;

    @Column(name = "info", columnDefinition = "TEXT")
    private String info;

    @Column(name = "link")
    private String link;

    @Column(name = "last_game")
    private Date lastGame;

    @Column(name = "last_update")
    private Date lastUpdate;

    @Enumerated(EnumType.STRING)
    @Column(name = "engine", nullable = false)
    private GameEngine engine = GameEngine.DEFAULT;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Language language = Language.DEFAULT;

    @Column(name = "view_type", nullable = false)
    private Integer viewType;

    @Column(name = "rate")
    private Double rate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_type_id", referencedColumnName = "id", nullable = false)
    private CardType cardType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_status_id", referencedColumnName = "id", nullable = false)
    private CardStatus cardStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_card_status_id", referencedColumnName = "id")
    private CardStatus oldCardStatus;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "card_tag_join",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<CardTypeTag> tagList;

}
