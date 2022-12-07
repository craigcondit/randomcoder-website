package org.randomcoder.website.cache;

import org.randomcoder.website.data.Tag;

import java.util.Date;

public record ArticlesByTagBeforeDateRangeKey(long tagId, long endDate, long offset, long limit) {

    public ArticlesByTagBeforeDateRangeKey(Tag tag, Date endDate, long offset, long limit) {
        this(tag.getId(), endDate.getTime(), offset, limit);
    }

}
