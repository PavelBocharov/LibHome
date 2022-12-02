package com.mar.ds.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "localization")
public class Localization implements Serializable, HasId {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "localization_seq")
    private Long id;

    @Column(name = "key", unique = true, nullable = false)
    private String key;

    @Column(name = "en", nullable = false)
    private String en;

    @Column(name = "ru", nullable = false)
    private String ru;

}
