package org.randomcoder.website.controller;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.UriInfo;
import org.apache.commons.lang3.StringUtils;
import org.randomcoder.website.bo.ArticleBusiness;
import org.randomcoder.website.bo.TagBusiness;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.Pagination;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Locale;

@Singleton
public class ArticleTagListController extends AbstractArticleListController<Tag> {

    private static final Logger logger = LoggerFactory.getLogger(ArticleTagListController.class);

    @Inject
    ArticleBusiness articleBusiness;

    @Inject
    TagBusiness tagBusiness;

    @Override
    protected Tag populateContext(UriInfo uriInfo) {
        String tagName = StringUtils.trimToEmpty(uriInfo.getPathParameters().getFirst("tagName")).toLowerCase(Locale.US);
        logger.debug("Tag name: " + tagName);

        return tagBusiness.findTagByName(tagName);
    }

    @Override
    protected Page<Article> listArticlesBeforeDate(Tag tag, Date cutoffDate, long offset, long length) {
        return articleBusiness.listArticlesByTagBeforeDate(tag, cutoffDate, offset, length);
    }

    @Override
    protected List<Article> listArticlesBetweenDates(Tag tag, Date startDate, Date endDate) {
        return articleBusiness.listArticlesByTagBetweenDates(tag, startDate, endDate);
    }

    @Override
    protected String getSubTitle(Tag tag) {
        return tag == null ? null : tag.getDisplayName();
    }

}

