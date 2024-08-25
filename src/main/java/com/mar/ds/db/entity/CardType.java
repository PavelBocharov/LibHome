package com.mar.ds.db.entity;

import com.mar.ds.views._build.popup.PopupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card_type")
public class CardType implements Serializable, HasId, PopupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_type_seq")
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

}
