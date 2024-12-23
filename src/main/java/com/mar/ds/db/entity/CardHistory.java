package com.mar.ds.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card_history")
public class CardHistory implements Serializable, HasId {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_history_seq_name")
    @SequenceGenerator(name = "card_history_seq_name", sequenceName = "card_history_seq", allocationSize = 1)
    private Long id;

    @Column(name = "editable_id")
    private Long editableId;

    @Column(name = "update_card_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateCardTime;

    @Column(name = "title_page", nullable = false)
    private String titlePage;

    @Column(name = "column_name", nullable = false)
    private String columnName;

    @Column(name = "old_value", nullable = false)
    private String oldValue;

    @Column(name = "new_value", nullable = false)
    private String newValue;

    @PrePersist
    public void preInsert() {
        updateCardTime = updateCardTime == null ? new Date() : updateCardTime;
    }

}
