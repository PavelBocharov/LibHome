package com.mar.libhome.db.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "card_type")
public class CardType implements MongoEntity<UUID> {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private String title;

    @Override
    public List<String> checkFields() {
        List<String> errors = new LinkedList<>();
        if (isBlank(title)) {
            errors.add("Card type title is blank.");
        }
        return errors;
    }
}
