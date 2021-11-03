package com.mar.randomvaadin.db.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "rand_task")
@NoArgsConstructor
public class RandTask {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "text", unique = true, nullable = false)
    private String text;


    public RandTask(String text) {
        this.text = text;
    }
}
