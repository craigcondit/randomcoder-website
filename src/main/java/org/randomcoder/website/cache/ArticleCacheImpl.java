package org.randomcoder.website.cache;

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

import java.time.Duration;
import java.util.List;

@Singleton
public class ArticleCacheImpl implements ArticleCache {

    private final Cache<ArticlesBetweenDatesKey, List<Article>> articlesBetweenDates;
    private final Cache<ArticlesBeforeDateRangeKey, Page<Article>> articlesBeforeDateRange;
    private final Cache<ArticlesByTagBetweenDatesKey, List<Article>> articlesByTagBetweenDates;
    private final Cache<ArticlesByTagBeforeDateRangeKey, Page<Article>> articlesByTagBeforeDateRange;
    private final Cache<Integer, List<Article>> articlesRecentLimit;

    @Inject
    public ArticleCacheImpl(MetricRegistry metrics) {

        articlesBetweenDates = Caffeine
                .newBuilder()
                .maximumWeight(10_000_000)
                .expireAfterAccess(Duration.ofMinutes(15))
                .weigher(this::articlesBetweenDatesWeight)
                .recordStats(() -> new MetricsStatsCounter(metrics, "cache.articles.between.dates"))
                .build();

        articlesBeforeDateRange = Caffeine
                .newBuilder()
                .maximumWeight(10_000_000)
                .expireAfterAccess(Duration.ofMinutes(15))
                .weigher(this::articlesBeforeDateRangeWeight)
                .recordStats(() -> new MetricsStatsCounter(metrics, "cache.articles.before.date.range"))
                .build();

        articlesByTagBetweenDates = Caffeine
                .newBuilder()
                .maximumWeight(10_000_000)
                .expireAfterAccess(Duration.ofMinutes(15))
                .weigher(this::articlesBetweenDatesWeight)
                .recordStats(() -> new MetricsStatsCounter(metrics, "cache.articles.by.tag.between.dates"))
                .build();

        articlesByTagBeforeDateRange = Caffeine
                .newBuilder()
                .maximumWeight(10_000_000)
                .expireAfterAccess(Duration.ofMinutes(15))
                .weigher(this::articlesBeforeDateRangeWeight)
                .recordStats(() -> new MetricsStatsCounter(metrics, "cache.articles.by.tag.before.date.range"))
                .build();

        articlesRecentLimit = Caffeine
                .newBuilder()
                .maximumSize(10)
                .recordStats(() -> new MetricsStatsCounter(metrics, "cache.articles.recent.limit"))
                .build();
    }

    @Override
    public void clearAll() {
        articlesBetweenDates.invalidateAll();
        articlesBeforeDateRange.invalidateAll();
        articlesByTagBetweenDates.invalidateAll();
        articlesByTagBeforeDateRange.invalidateAll();
        articlesRecentLimit.invalidateAll();
    }

    private int articlesBetweenDatesWeight(Object key, List<Article> value) {
        return 100 + value.stream()
                .mapToInt(this::articleWeight)
                .sum();
    }

    private int articlesBeforeDateRangeWeight(Object key, Page<Article> value) {
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

    @Override
    public Cache<ArticlesByTagBetweenDatesKey, List<Article>> articlesByTagBetweenDates() {
        return articlesByTagBetweenDates;
    }

    @Override
    public Cache<ArticlesByTagBeforeDateRangeKey, Page<Article>> articlesByTagBeforeDateRange() {
        return articlesByTagBeforeDateRange;
    }

    @Override
    public Cache<Integer, List<Article>> articlesRecentLimit() {
        return articlesRecentLimit;
    }

}
