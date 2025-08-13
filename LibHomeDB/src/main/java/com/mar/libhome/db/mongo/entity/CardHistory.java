package com.mar.libhome.db.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "card_history")
public class CardHistory implements MongoEntity<UUID> {

    @Id
    private UUID id;

    @Field(name = "editable_id")
    private UUID editableId;

    @Field(name = "update_card_time")
    private Date updateCardTime = new Date();

    @Field(name = "title_page")
    private String titlePage;

    @Field(name = "column_name")
    private String columnName;

    @Field(name = "old_value")
    private String oldValue;

    @Field(name = "new_value")
    private String newValue;

    @Override
    public List<String> checkFields() {
        List<String> errors = new LinkedList<>();
        if (isNull(updateCardTime)) {
            errors.add("History update time is null.");
        }
        if (isBlank(titlePage)) {
            errors.add("History page title is blank.");
        }
        if (isBlank(columnName)) {
            errors.add("History update column name is blank.");
        }
        if (isNull(oldValue)) {
            errors.add("History column old value is null.");
        }
        if (isNull(newValue)) {
            errors.add("History column new value is null.");
        }
        return errors;
    }

}
