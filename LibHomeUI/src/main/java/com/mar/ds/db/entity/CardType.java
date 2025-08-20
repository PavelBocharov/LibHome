package com.mar.ds.db.entity;

import com.mar.ds.views.build.popup.PopupEntity;
import com.mar.libhome.dto.HasId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card_type")
public class CardType implements Serializable, HasId, PopupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_type_seq")
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @OneToMany(mappedBy = "id")
    @LazyCollection(LazyCollectionOption.FALSE)
    List<CardTypeTag> tags;

    @Override
    public Long getLongId() {
        return id;
    }
}
