package org.randomcoder.website.cache;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.caffeine.MetricsStatsCounter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.ConcurrentStatsCounter;
import com.github.benmanes.caffeine.cache.stats.StatsCounter;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.Page;

import java.util.List;

@Singleton
public class ArticleCacheImpl implements ArticleCache {

    private final Cache<ArticlesBetweenDatesKey, List<Article>> articlesBetweenDates;
    private final Cache<ArticlesBeforeDateRangeKey, Page<Article>> articlesBeforeDateRange;

    @Inject
    public ArticleCacheImpl(MetricRegistry metrics) {

        articlesBetweenDates = Caffeine
                .newBuilder()
                .maximumWeight(10_000_000)
                .weigher(this::articlesBetweenDatesWeight)
                .recordStats(() -> new MetricsStatsCounter(metrics, "cache.articles.between.dates"))
                .build();

        metrics.gauge("cache.articles.between.dates.size",
                () -> ((Gauge<Long>) articlesBetweenDates::estimatedSize));

        articlesBeforeDateRange = Caffeine
                .newBuilder()
                .maximumWeight(10_000_000)
                .weigher(this::articlesBeforeDateRangeWeight)
                .recordStats(() -> new MetricsStatsCounter(metrics, "cache.articles.before.date.range"))
                .build();

        metrics.gauge("cache.articles.before.date.range.size",
                () -> ((Gauge<Long>) articlesBetweenDates::estimatedSize));
    }

    @Override
    public void clearAll() {
        articlesBetweenDates.invalidateAll();
        articlesBeforeDateRange.invalidateAll();
    }

    private int articlesBetweenDatesWeight(ArticlesBetweenDatesKey key, List<Article> value) {
        return 100 + value.stream()
                .mapToInt(this::articleWeight)
                .sum();
    }

    private int articlesBeforeDateRangeWeight(ArticlesBeforeDateRangeKey key, Page<Article> value) {
        return 100 + value.getContent().stream()
                .mapToInt(this::articleWeight)
                .sum();
    }

    private int articleWeight(Article article) {
        int size = 100;
        var content = article.getContent();
        if (content != null) {
            size += content.length();
        }
        var summary = article.getSummary();
        if (summary != null) {
            size += summary.length();
        }
        return size;
    }

    private StatsCounter getStatsCounter() {
        return new ConcurrentStatsCounter();
    }

    @Override
    public Cache<ArticlesBetweenDatesKey, List<Article>> articlesBetweenDates() {
        return articlesBetweenDates;
    }

    @Override
    public Cache<ArticlesBeforeDateRangeKey, Page<Article>> articlesBeforeDateRange() {
        return articlesBeforeDateRange;
    }

}
