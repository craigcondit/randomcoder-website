package org.randomcoder.website.feed;

import org.randomcoder.website.data.Article;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FeedInfo {

    private String title;
    private String subtitle;
    private URL feedUrl;
    private URL altUrl;
    private String feedId;
    private List<Article> articles;

    public FeedInfo() {
        articles = new ArrayList<Article>();
    }

    public FeedInfo(String title, String subtitle, URL feedUrl, URL altUrl, String feedId, List<Article> articles) {
        super();
        this.title = title;
        this.subtitle = subtitle;
        this.feedUrl = feedUrl;
        this.altUrl = altUrl;
        this.feedId = feedId;
        this.articles = articles;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public URL getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(URL feedUrl) {
        this.feedUrl = feedUrl;
    }

    public URL getAltUrl() {
        return altUrl;
    }

    public void setAltUrl(URL altUrl) {
        this.altUrl = altUrl;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

}
