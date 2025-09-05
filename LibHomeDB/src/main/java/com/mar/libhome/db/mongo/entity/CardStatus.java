package com.mar.libhome.db.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
@Document(collection = "card_status")
public class CardStatus implements MongoEntity<UUID> {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private String title;

    @Indexed(unique = true)
    private String color;

    private String icon;

    @Field("is_rate")
    private Boolean isRate;

    @Field("has_upd_status")
    private Boolean hasUpdStatus;

    @Field("tech_id")
    private String tech;

    @Field("sort_order")
    private Long order;

    @Override
    public List<String> checkFields() {
        List<String> errors = new LinkedList<>();
        if (isBlank(title)) {
            errors.add("Status title is blank.");
        }
        if (isBlank(color)) {
            errors.add("Status color is blank.");
        }
        if (isBlank(icon)) {
            errors.add("Status icon is blank.");
        }
        if (isNull(isRate)) {
            errors.add("Calculate status rate flag is null.");
        }
        if (isNull(hasUpdStatus)) {
            errors.add("Status has update flag is null.");
        }
        if (isNull(order)) {
            errors.add("Status order is null.");
        }
        return errors;
    }
}
