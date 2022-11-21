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
@Table(name = "action")
public class Action implements Serializable, HasId {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "action_seq")
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @OneToOne(optional = true)
    private Item needItem;

    @OneToOne(optional = true)
    private Mission needMission;

    @OneToOne(optional = true)
    private Task needTask;

    @Column(name = "move_mission", nullable = false)
    private Boolean moveMission;

    @ManyToOne
    @JoinColumn(name="dialog_id", nullable=true)
    private Dialog dialog;

    @OneToOne(optional = true)
    private Dialog openedDialog;

    @Column(name = "is_teleport")
    private Boolean isTeleport;

    @Column(name = "level")
    private String level;

    @Column(name = "position_x")
    private Float positionX;

    @Column(name = "position_y")
    private Float positionY;

    @Column(name = "position_z")
    private Float positionZ;

    @Column(name = "rotation_x")
    private Float rotationX;

    @Column(name = "rotation_y")
    private Float rotationY;

    @Column(name = "rotation_z")
    private Float rotationZ;
}
