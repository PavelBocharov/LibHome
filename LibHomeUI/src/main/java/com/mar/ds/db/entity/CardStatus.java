package com.mar.ds.db.entity;

import com.mar.libhome.dto.HasId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Статус карточки.
 */
@Getter
@Setter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card_status")
@Deprecated
public class CardStatus implements Serializable, HasId {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_status_seq")
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "color", nullable = false, unique = true)
    private String color;

    @Column(name = "icon", nullable = false, unique = false)
    @ColumnDefault("BULLSEYE")
    private String icon;

    @Column(name = "is_rate", nullable = false)
    @ColumnDefault("true")
    private Boolean isRate;

    @Column(name = "has_upd_status", nullable = false)
    @ColumnDefault("false")
    private Boolean hasUpdStatus;

    @Column(name = "tech_id")
    private String tech;

    @Column(name = "sort_order")
    private Long order;

    public boolean isTech() {
        return tech != null && !tech.isBlank();
    }

    @Override
    public Long getLongId() {
        return id;
    }
}
