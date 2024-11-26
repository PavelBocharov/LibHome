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

    public static List<CardHistory> compare(CardDto oldCard, CardDto updatedCard) {
        if (oldCard == null) {
            if (updatedCard.getId() != null) {
                return List.of(
                        CardHistory.builder()
                                .titlePage(updatedCard.getViewType().getTitle())
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
                .append("title", oldCard.getTitle(), updatedCard.getTitle())
                .append("point", oldCard.getPoint(), updatedCard.getPoint())
                .append("info", oldCard.getInfo(), updatedCard.getInfo())
                .append("link", oldCard.getLink(), updatedCard.getLink())
                .append("lastGame", oldCard.getLastGame().getTime(), updatedCard.getLastGame().getTime())
                .append("lastUpdate", oldCard.getLastUpdate().getTime(), updatedCard.getLastUpdate().getTime())
                .append("language", oldCard.getLanguage(), updatedCard.getLanguage())
                .append("viewType", oldCard.getViewType(), updatedCard.getViewType())
                .append("rate", oldCard.getRate(), updatedCard.getRate())
                .append("cardType", oldCard.getCardType(), updatedCard.getCardType())
                .append("cardStatus", oldCard.getCardStatus(), updatedCard.getCardStatus())
                .append("oldCardStatus", oldCard.getOldCardStatus(), updatedCard.getOldCardStatus());

        Set<Long> oldTags = oldCard.getTagList().stream().map(CardTypeTagDto::getId).collect(Collectors.toSet());
        Set<Long> newTags = updatedCard.getTagList().stream().map(CardTypeTagDto::getId).collect(Collectors.toSet());
        for (Long tagId : oldTags) {
            if (!newTags.contains(tagId)) {
                diffBuilder.append("REMOVE tag", tagId, "");
            }
        }

        for (Long tagId : newTags) {
            if (!oldTags.contains(tagId)) {
                diffBuilder.append("ADD tag", "", tagId);
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
                            .titlePage(oldCard.getViewType().getTitle())
                            .oldValue(String.valueOf(diff.getLeft()))
                            .newValue(String.valueOf(diff.getRight()))
                            .updateCardTime(updDate)
                            .build()
            );
        }
        return historyList;
    }

}
