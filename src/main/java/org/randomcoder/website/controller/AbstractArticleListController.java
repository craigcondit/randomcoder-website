package org.randomcoder.website.controller;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.UriInfo;
import org.randomcoder.website.Config;
import org.randomcoder.website.bo.TagBusiness;
import org.randomcoder.website.contentfilter.ContentFilter;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.model.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class AbstractArticleListController<T> {

    private static final String PARAM_YEAR = "year";
    private static final String PARAM_MONTH = "month";
    private static final String PARAM_DAY = "day";
    private static final String PARAM_PAGE_NUMBER = "page.page";
    private static final String PARAM_PAGE_SIZE = "page.size";

    @Inject
    TagBusiness tagBusiness;

    @Inject
    ContentFilter contentFilter;

    @Inject
    @Named(Config.ARTICLE_PAGESIZE_MAX)
    Long maximumPageSize = 50L;

    abstract protected T populateContext(UriInfo uriInfo);

    abstract protected List<Article> listArticlesBetweenDates(T context, Date startDate, Date endDate);

    abstract protected Page<Article> listArticlesBeforeDate(T context, Date cutoffDate, long offset, long length);

    abstract protected String getSubTitle(T context);

    public Map<String, ? extends Object> buildModel(UriInfo uriInfo) {
        var context = populateContext(uriInfo);

        Map<String, Object> model = new HashMap<>();

        // set range and sort order
        var oal = PageUtils.parsePagination(uriInfo, 10, maximumPageSize);
        long offset = oal.offset();
        long length = oal.length();

        // get current month
        Calendar currentMonth = Calendar.getInstance();
        currentMonth.setTime(new Date());

        int year = getIntQueryParam(uriInfo, PARAM_YEAR, 0);
        int month = getIntQueryParam(uriInfo, PARAM_MONTH, 0);
        int day = getIntQueryParam(uriInfo, PARAM_DAY, 0);

        if (year > 0 && month > 0) {
            currentMonth.set(Calendar.YEAR, year);
            currentMonth.set(Calendar.MONTH, month - 1);
        }

        currentMonth.set(Calendar.DAY_OF_MONTH, 1);
        currentMonth.set(Calendar.HOUR_OF_DAY, 0);
        currentMonth.set(Calendar.MINUTE, 0);
        currentMonth.set(Calendar.SECOND, 0);
        currentMonth.set(Calendar.MILLISECOND, 0);

        // get next month
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.setTime(currentMonth.getTime());
        nextMonth.add(Calendar.MONTH, 1);
        nextMonth.set(Calendar.DAY_OF_MONTH, 1);
        nextMonth.set(Calendar.HOUR_OF_DAY, 0);
        nextMonth.set(Calendar.MINUTE, 0);
        nextMonth.set(Calendar.SECOND, 0);
        nextMonth.set(Calendar.MILLISECOND, 0);

        // mark calendar with days containing articles
        boolean[] days = new boolean[31];
        for (int i = 0; i < 31; i++) {
            days[i] = false;
        }

        Calendar cal = Calendar.getInstance();
        for (Article article : listArticlesBetweenDates(context, currentMonth.getTime(), nextMonth.getTime())) {
            cal.setTime(article.getCreationDate());
            days[cal.get(Calendar.DAY_OF_MONTH) - 1] = true;
        }

        Calendar cutoff = Calendar.getInstance();
        cutoff.setTime(currentMonth.getTime());

        if (year > 0 && month > 0 && day > 0) {
            cutoff.set(Calendar.DAY_OF_MONTH, day);
            cutoff.add(Calendar.DAY_OF_MONTH, 1);
        } else {
            cutoff.add(Calendar.MONTH, 1);
        }

        cutoff.set(Calendar.HOUR_OF_DAY, 0);
        cutoff.set(Calendar.MINUTE, 0);
        cutoff.set(Calendar.SECOND, 0);
        cutoff.set(Calendar.MILLISECOND, 0);

        // load articles
        Page<Article> articles = listArticlesBeforeDate(context, cutoff.getTime(), offset, length);

        // wrap article list
        List<ArticleDecorator> wrappedArticles = new ArrayList<>(articles.getContent().size());
        for (Article article : articles.getContent()) {
            wrappedArticles.add(new ArticleDecorator(article, contentFilter));
        }

        // get tag cloud
        List<TagCloudEntry> tagCloud = tagBusiness.getTagCloud();

        // populate model
        model.put("articles", wrappedArticles);
        model.put("pager", articles);
        model.put("pagerInfo", new PagerInfo<>(articles, uriInfo));
        model.put("days", days);
        model.put("tagCloud", tagCloud);
        model.put("calendar", new CalendarInfo(uriInfo, days));

        String subTitle = getSubTitle(context);
        if (subTitle != null) {
            model.put("pageSubTitle", subTitle);
        }

        return model;
    }

    private static int getIntQueryParam(UriInfo uriInfo, String param, int defaultValue) {
        return (int) getLongQueryParam(uriInfo, param, defaultValue);
    }

    private static long getLongQueryParam(UriInfo uriInfo, String param, long defaultValue) {
        String value = uriInfo.getQueryParameters().getFirst(param);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
