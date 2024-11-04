package com.mar.ds.db.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lib_home_sequence")
public class LibHomeSequence implements Serializable {

    @Id
    private String id;

    @Column(name = "seq_name", unique = true, nullable = false)
    private String seqName;

    @Column(name = "seq_val")
    private Long seqValue;

    @PrePersist
    public void preInsert() {
        id = UUID.randomUUID().toString();
        seqValue = 0L;
    }

}
