package com.mar.ds.views._build.pagination_grid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaginationPageUtilsTest {

    @Test
    void getUpperNumb() {
        assertEquals("¹", PaginationPageUtils.getUpperNumb(1L));
        assertEquals("¹²", PaginationPageUtils.getUpperNumb(12L));
        assertEquals("¹²³⁴", PaginationPageUtils.getUpperNumb(1_234L));
        assertEquals("¹²³⁴⁵⁶⁷", PaginationPageUtils.getUpperNumb(1_234_567L));
        assertEquals("⁻⁸⁰⁹", PaginationPageUtils.getUpperNumb(-809L));
    }
}