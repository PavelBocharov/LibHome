package com.mar.ds.db.diff;

import com.mar.ds.db.dto.CardDto;
import com.mar.ds.db.dto.CardTypeTagDto;
import com.mar.ds.db.entity.CardHistory;
import org.apache.commons.lang3.builder.Diff;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DiffCard {

    public static final String CARD_TITLE = "title";
    public static final String CARD_POINT = "point";
    public static final String CARD_INFO = "info";
    public static final String CARD_LINK = "link";
    public static final String CARD_LAST_GAME_DATE = "lastGame";
    public static final String CARD_LAST_UPD_DATE = "lastUpdate";
    public static final String CARD_LANGUAGE = "language";
    public static final String CARD_VIEW_TYPE = "viewType";
    public static final String CARD_RATE = "rate";
    public static final String CARD_TYPE = "cardType";
    public static final String CARD_STATUS = "cardStatus";
    public static final String CARD_OLD_STATUS = "oldCardStatus";
    public static final String CARD_TAG = "tag";
    public static final String CARD_ENGINE = "engine";

    public static List<CardHistory> compare(CardDto oldCard, CardDto updatedCard) {
        if (oldCard == null) {
            if (updatedCard.getId() != null) {
                return List.of(
                        CardHistory.builder()
                                .titlePage(String.valueOf(updatedCard.getViewType()))
                                .columnName("CREATE CARD")
                                .oldValue("")
                                .newValue(updatedCard.toString())
                                .editableId(updatedCard.getId())
                                .build()
                );
            } else {
                throw new RuntimeException("Diff after save - need card ID.");
            }
        }

        DiffBuilder<CardDto> diffBuilder = new DiffBuilder<CardDto>(oldCard, updatedCard, ToStringStyle.DEFAULT_STYLE)
                .append(CARD_ENGINE, oldCard.getEngine(), updatedCard.getEngine())
                .append(CARD_TITLE, oldCard.getTitle(), updatedCard.getTitle())
                .append(CARD_POINT, oldCard.getPoint(), updatedCard.getPoint())
                .append(CARD_INFO, oldCard.getInfo(), updatedCard.getInfo())
                .append(CARD_LINK, oldCard.getLink(), updatedCard.getLink())
                .append(CARD_LAST_GAME_DATE, oldCard.getLastGame().getTime(), updatedCard.getLastGame().getTime())
                .append(CARD_LAST_UPD_DATE, oldCard.getLastUpdate().getTime(), updatedCard.getLastUpdate().getTime())
                .append(CARD_LANGUAGE, oldCard.getLanguage(), updatedCard.getLanguage())
                .append(CARD_VIEW_TYPE, oldCard.getViewType(), updatedCard.getViewType())
                .append(CARD_TYPE, oldCard.getCardType(), updatedCard.getCardType())
                .append(CARD_STATUS, oldCard.getCardStatus(), updatedCard.getCardStatus())
                .append(CARD_OLD_STATUS, oldCard.getOldCardStatus(), updatedCard.getOldCardStatus());

        Set<Long> oldTags = oldCard.getTagList().stream().map(CardTypeTagDto::getId).collect(Collectors.toSet());
        Set<Long> newTags = updatedCard.getTagList().stream().map(CardTypeTagDto::getId).collect(Collectors.toSet());
        for (Long tagId : oldTags) {
            if (!newTags.contains(tagId)) {
                diffBuilder.append("REMOVE " + CARD_TAG, tagId, "");
            }
        }

        for (Long tagId : newTags) {
            if (!oldTags.contains(tagId)) {
                diffBuilder.append("ADD " + CARD_TAG, "", tagId);
            }
        }

        DiffResult<CardDto> res = diffBuilder.build();
        List<CardHistory> historyList = new ArrayList<>(res.getDiffs().size());
        Date updDate = new Date();
        for (Diff<?> diff : res) {
            historyList.add(
                    CardHistory.builder()
                            .editableId(oldCard.getId())
                            .columnName(diff.getFieldName())
                            .titlePage(String.valueOf(oldCard.getViewType()))
                            .oldValue(String.valueOf(diff.getLeft()))
                            .newValue(String.valueOf(diff.getRight()))
                            .updateCardTime(updDate)
                            .build()
            );
        }
        return historyList;
    }

}
