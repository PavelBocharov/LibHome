package com.mar.ds.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dialog")
public class Dialog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dialog_seq")
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @OneToOne(optional = false)
    private Character character;

    @OneToMany(mappedBy = "dialog", fetch = FetchType.EAGER)
    private List<Item> items;

    @OneToMany(mappedBy = "dialog", fetch = FetchType.EAGER)
    private List<Action> actions;

}
