package org.randomcoder.website.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.Page;

import java.util.List;

public interface ArticleCache {

    void clearAll();

    Cache<ArticlesBetweenDatesKey, List<Article>> articlesBetweenDates();

    Cache<ArticlesBeforeDateRangeKey, Page<Article>> articlesBeforeDateRange();

}
