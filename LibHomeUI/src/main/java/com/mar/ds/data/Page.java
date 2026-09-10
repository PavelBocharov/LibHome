package com.mar.ds.data;

import com.mar.libhome.api.data.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {

    private Collection<T> content;
    private PageRequest page;
    private long totalCount;

    public Long getTotalPages() {
        int pageSize = page.getPageSize();
        if (pageSize <= 0) {
            return 0L;
        }
        BigDecimal a = new BigDecimal(totalCount);
        BigDecimal b = new BigDecimal(pageSize);
        return a.divide(b, 0, RoundingMode.CEILING).longValue();
    }

}
