package com.mar.libhome.controller.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sort {
        private Map<String, Direction> order = new HashMap<>();

        public enum Direction {
            ASC, DESC
        }
    }

}
