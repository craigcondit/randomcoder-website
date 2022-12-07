package org.randomcoder.website.cache;

import org.randomcoder.website.data.Tag;

import java.util.Date;

public record ArticlesByTagBetweenDatesKey(long tagId, long startDate, long endDate) {

    public ArticlesByTagBetweenDatesKey(Tag tag, Date startDate, Date endDate) {
        this(tag.getId(), startDate.getTime(), endDate.getTime());
    }

}
