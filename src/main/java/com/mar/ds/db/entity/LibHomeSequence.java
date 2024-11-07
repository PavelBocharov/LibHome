package com.mar.ds.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

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
