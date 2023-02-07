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
@Table(name = "generate_type")
public class GenerateType implements Serializable, HasId {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generate_type_seq")
    private Long id;

    @Column(name = "enum_number", unique = true, nullable = false)
    private Long enumNumber;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

}
