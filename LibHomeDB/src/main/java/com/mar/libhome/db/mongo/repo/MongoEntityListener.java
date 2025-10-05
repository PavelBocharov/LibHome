package com.mar.libhome.db.mongo.repo;

import com.mar.libhome.db.mongo.entity.MongoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Component
public class MongoEntityListener extends AbstractMongoEventListener<MongoEntity> {

    private final Logger log = LoggerFactory.getLogger(MongoEntityListener.class);


    @Override
    public void onBeforeConvert(BeforeConvertEvent<MongoEntity> event) {
        MongoEntity entity = event.getSource();
        if (entity.isNew()) {
            entity.setId(UUID.randomUUID());
        }
        List<String> errors = entity.checkFields();
        if (!isEmpty(errors)) {
            throw new RuntimeException("Entity with error. Dto: " + entity + ", errors: " + errors);
        }
    }

}