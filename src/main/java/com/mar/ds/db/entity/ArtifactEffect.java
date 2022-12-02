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
@Table(name = "artifact_effect")
public class ArtifactEffect implements Serializable, HasId {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "artifact_effect_seq")
    private Long id;

    @Column(name = "enum_status", unique = true, nullable = false)
    private Long enumNumber;

    @Column(name = "title", unique = true, nullable = false)
    private String title;

    @Column(name = "info", nullable = false)
    private String info;

}
