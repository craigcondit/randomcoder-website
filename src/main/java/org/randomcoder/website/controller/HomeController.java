package org.randomcoder.website.controller;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.bo.ArticleBusiness;
import org.randomcoder.website.data.Article;

import java.util.List;
import java.util.Date;

@Singleton
public class HomeController extends AbstractArticleListController {

    @Inject
    ArticleBusiness articleBusiness;

    @Override
    protected Page<Article> listArticlesBeforeDate(Date cutoffDate, long offset, long length) {
        return articleBusiness.listArticlesBeforeDate(cutoffDate, offset, length);
    }

    @Override
    protected List<Article> listArticlesBetweenDates(Date startDate, Date endDate) {
        return articleBusiness.listArticlesBetweenDates(startDate, endDate);
    }

    @Override
    protected String getSubTitle() {
        return null;
    }

}
