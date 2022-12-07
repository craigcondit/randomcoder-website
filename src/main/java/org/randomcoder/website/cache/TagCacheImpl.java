package org.randomcoder.website.cache;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.caffeine.MetricsStatsCounter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.randomcoder.website.data.TagStatistics;

import java.time.Duration;
import java.util.List;

@Singleton
public class TagCacheImpl implements TagCache {

    private final Cache<EmptyKey, Integer> maxArticleCount;
    private final Cache<EmptyKey, List<TagStatistics>> tagStatistics;

    @Inject
    public TagCacheImpl(MetricRegistry metrics) {
        maxArticleCount = Caffeine
                .newBuilder()
                .maximumSize(1)
                .expireAfterAccess(Duration.ofMinutes(15))
                .recordStats(() -> new MetricsStatsCounter(metrics, "cache.tags.max.article.count"))
                .build();

        tagStatistics = Caffeine
                .newBuilder()
                .maximumSize(1)
                .expireAfterAccess(Duration.ofMinutes(15))
                .recordStats(() -> new MetricsStatsCounter(metrics, "cache.tags.statistics"))
                .build();
    }

    @Override
    public void clearAll() {
        maxArticleCount.invalidateAll();
        tagStatistics.invalidateAll();
    }

    @Override
    public Cache<EmptyKey, Integer> maxArticleCount() {
        return maxArticleCount;
    }

    @Override
    public Cache<EmptyKey, List<TagStatistics>> tagStatistics() {
        return tagStatistics;
    }

}
