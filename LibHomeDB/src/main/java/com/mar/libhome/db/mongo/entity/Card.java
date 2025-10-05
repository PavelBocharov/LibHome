package com.mar.libhome.db.mongo.entity;

import com.mar.libhome.enums.GameEngine;
import com.mar.libhome.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
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
@Document(collection = "card")
@CompoundIndex(unique = true, def = "{'title': 1, 'view_type': 1}")
public class Card implements MongoEntity<UUID> {

    @Id
    private UUID id;

    private String title;

    private Double point;

    private String info;

    private String link;

    @Field("last_game")
    private Date lastGame;

    @Field("last_update")
    private Date lastUpdate;

    private GameEngine engine;

    private Language language = Language.DEFAULT;

    @Field("view_type")
    private Integer viewType;

    @Field("card_rate")
    private Double rate;

    @Field("card_type_id")
    private UUID cardTypeId;

    @Field("card_status_id")
    private UUID cardStatusId;

    @Field("old_card_status_id")
    private UUID oldCardStatusId;

    @Field("tag_id_list")
    private List<UUID> tagIdList;

    @Override
    public List<String> checkFields() {
        List<String> errors = new LinkedList<>();
        if (isNull(title)) {
            errors.add("Card title is null.");
        }
        if (isBlank(title)) {
            errors.add("Card title is blank.");
        }
        if (isNull(viewType)) {
            errors.add("Card view type is null.");
        }
        if (isNull(cardTypeId)) {
            errors.add("Card type ID is null.");
        }
        if (isNull(cardStatusId)) {
            errors.add("Card status ID is null.");
        }
        return errors;
    }
}
