package org.randomcoder.website.cache;

import java.util.Date;

public record ArticlesBetweenDatesKey(long startDate, long endDate) {

    public ArticlesBetweenDatesKey(Date startDate, Date endDate) {
        this(startDate.getTime(), endDate.getTime());
    }

}
