package com.mar.libhome.db.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "card_status")
public class CardStatus {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @NotBlank
    @Column(name = "color", nullable = false, unique = true)
    private String color;

    @NotBlank
    @Column(name = "icon", nullable = false)
    private String icon;

    @Column(name = "is_rate", nullable = false)
    private Boolean isRate;

    @Column(name = "has_upd_status", nullable = false)
    private Boolean hasUpdStatus;

    @Column(name = "tech_id")
    private String tech;

    @Column(name = "sort_order", nullable = false)
    private Long order;

}
