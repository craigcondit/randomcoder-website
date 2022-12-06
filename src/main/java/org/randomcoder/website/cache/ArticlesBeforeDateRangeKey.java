package org.randomcoder.website.cache;

import java.util.Date;

public record ArticlesBeforeDateRangeKey(long endDate, long offset, long limit) {

    public ArticlesBeforeDateRangeKey(Date endDate, long offset, long limit) {
        this(endDate.getTime(), offset, limit);
    }

}
