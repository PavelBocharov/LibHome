package com.mar.libhome.controller.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.LinkedHashMap;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequest {

    private Integer pageNumber;
    private Integer pageSize;
    private Sort sort;

    public static PageRequest of(int page, int size) {
        assert page >= 0;
        assert size > 0;

        return PageRequest.builder().pageNumber(page).pageSize(size).build();
    }

    public static PageRequest of(int page, int size, PageRequest.Sort sort) {
        assert page >= 0;
        assert size > 0;

        return PageRequest.builder().pageNumber(page).pageSize(size).sort(sort).build();
    }

    public record Sort(LinkedHashMap<String, Direction> order) {
        public Sort(LinkedHashMap<String, Direction> order) {
            this.order = new LinkedHashMap<>(order != null ? Collections.unmodifiableMap(order) : Collections.emptyMap());
        }

        @Override
        public LinkedHashMap<String, Direction> order() {
            return new LinkedHashMap<>(order);
        }

        public enum Direction {
            ASC, DESC
        }
    }

}
