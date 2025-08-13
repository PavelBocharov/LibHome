package com.mar.libhome.db.mongo.entity;

import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.List;

public interface MongoEntity<ID> extends Serializable, Persistable<ID> {

    ID getId();

    void setId(ID id);

    List<String> checkFields();

    @Override
    default boolean isNew() {
        return getId() == null;
    }

}
