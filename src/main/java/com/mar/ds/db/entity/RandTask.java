package com.mar.ds.db.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "rand_task")
@NoArgsConstructor
public class RandTask implements Serializable, HasId {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "rand_task_seq")
    private Long id;

    @Column(name = "text", unique = true, nullable = false)
    private String text;


    public RandTask(String text) {
        this.text = text;
    }
}
