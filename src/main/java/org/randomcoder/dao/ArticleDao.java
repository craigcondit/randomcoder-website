package org.randomcoder.dao;

import org.randomcoder.db.Article;
import org.randomcoder.db.Tag;

import java.util.Date;
import java.util.List;

public interface ArticleDao {

    void deleteById(long articleId);

    Article findById(long articleId);

    Article findByPermalink(String permalink);

    Page<Article> listBeforeDate(Date endDate, long offset, long length);

    Page<Article> listByTagBeforeDate(Tag tag, Date endDate, long offset, long length);

    List<Article> listBetweenDates(Date startDate, Date endDate);

    List<Article> listByTagBetweenDates(Tag tag, Date startDate, Date endDate);

}
