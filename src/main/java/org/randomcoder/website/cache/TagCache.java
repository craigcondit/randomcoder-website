package org.randomcoder.website.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.randomcoder.website.data.TagStatistics;

import java.util.List;

public interface TagCache {

    void clearAll();

    Cache<EmptyKey, Integer> maxArticleCount();

    Cache<EmptyKey, List<TagStatistics>> tagStatistics();

}
