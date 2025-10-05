package com.mar.libhome.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRs implements Serializable {

    Long total;
    Integer page;
    Integer size;
    List<CardDto> cards;

    @Override
    public String toString() {
        return "CardRs{" +
                "total=" + total +
                ", page=" + page +
                ", size=" + size +
                ", cards.size=" + Optional.ofNullable(cards).orElse(Collections.emptyList()).size() +
                '}';
    }
}
