package org.randomcoder.website.dao;

import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.Tag;

import java.util.Date;
import java.util.List;

public interface ArticleDao {

    long save(Article article);

    void deleteById(long articleId);

    Article findById(long articleId);

    Article findByPermalink(String permalink);

    Page<Article> listByDateDesc(long offset, long length);

    Page<Article> listBeforeDate(Date endDate, long offset, long length);

    Page<Article> listByTagBeforeDate(Tag tag, Date endDate, long offset, long length);

    List<Article> listBetweenDates(Date startDate, Date endDate);

    List<Article> listByTagBetweenDates(Tag tag, Date startDate, Date endDate);

}
