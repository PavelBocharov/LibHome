package com.mar.libhome.db.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "card_history")
public class CardHistory {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "editable_id")
    private UUID editableId;

    @Column(name = "update_card_time", nullable = false)
    private Date updateCardTime = new Date();

    @NotBlank
    @Column(name = "title_page", nullable = false)
    private String titlePage;

    @NotBlank
    @Column(name = "column_name", nullable = false)
    private String columnName;

    @Column(name = "old_value", nullable = false, columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", nullable = false, columnDefinition = "TEXT")
    private String newValue;

}
