package org.randomcoder.website.controller;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.UriInfo;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.bo.ArticleBusiness;
import org.randomcoder.website.data.Article;

import java.util.List;
import java.util.Date;

@Singleton
public class HomeController extends AbstractArticleListController<Void> {

    @Inject
    ArticleBusiness articleBusiness;

    @Override
    protected Void populateContext(UriInfo uriInfo) {
        return null;
    }

    @Override
    protected Page<Article> listArticlesBeforeDate(Void context, Date cutoffDate, long offset, long length) {
        return articleBusiness.listArticlesBeforeDate(cutoffDate, offset, length);
    }

    @Override
    protected List<Article> listArticlesBetweenDates(Void context, Date startDate, Date endDate) {
        return articleBusiness.listArticlesBetweenDates(startDate, endDate);
    }

    @Override
    protected String getSubTitle(Void context) {
        return null;
    }

}
