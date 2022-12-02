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
@Table(name = "item")
public class Item implements Serializable, HasId {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "info", nullable = false)
    private String info;

    @Column(name = "shortInfo", nullable = false)
    private String shortInfo;

    @OneToOne(optional = false)
    private ItemStatus status;

    @OneToOne(optional = false)
    private ItemType type;

    @Column(name = "imgPath", nullable = false)
    private String imgPath;

    @Column(name = "needManna")
    private Float needManna;

    @Column(name = "healthDamage")
    private Float healthDamage;

    @Column(name = "mannaDamage")
    private Float mannaDamage;

    @Column(name = "reloadTick")
    private Float reloadTick;

    @Column(name = "objPath")
    private String objPath;

    @Column(name = "level")
    private String level;

    @Column(name = "positionX")
    private Float positionX;

    @Column(name = "positionY")
    private Float positionY;

    @Column(name = "positionZ")
    private Float positionZ;

    @Column(name = "rotationX")
    private Float rotationX;

    @Column(name = "rotationY")
    private Float rotationY;

    @Column(name = "rotationZ")
    private Float rotationZ;

    @ManyToOne
    @JoinColumn(name="dialog_id", nullable=true)
    private Dialog dialog;

    @OneToOne(optional = true)
    private ArtifactEffect artifactEffect;
}
