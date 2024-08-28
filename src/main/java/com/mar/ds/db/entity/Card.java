package com.mar.ds.db.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_seq")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "point", nullable = true)
    private Double point;

    @Column(name = "preview_image", nullable = false)
    private String previewImage;

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

}
