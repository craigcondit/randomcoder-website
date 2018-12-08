package org.randomcoder.feed;

import org.randomcoder.db.Article;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaBean which holds information about a syndicated feed.
 */
public class FeedInfo implements Serializable {
  private static final long serialVersionUID = -2535221591939158773L;

  private String title;
  private String subtitle;
  private URL feedUrl;
  private URL altUrl;
  private String feedId;
  private List<Article> articles;

  /**
   * Creates an uninitialized feed info object.
   */
  public FeedInfo() {
    articles = new ArrayList<Article>();
  }

  /**
   * Creates a new feed info object.
   *
   * @param title    article title
   * @param subtitle article subtitle, or <code>null</code> to omit
   * @param feedUrl  canonical feed URL
   * @param altUrl   alternate URL of content
   * @param feedId   unique feed identifier
   * @param articles list of articles to include
   */
  public FeedInfo(String title, String subtitle, URL feedUrl, URL altUrl,
      String feedId, List<Article> articles) {
    super();
    this.title = title;
    this.subtitle = subtitle;
    this.feedUrl = feedUrl;
    this.altUrl = altUrl;
    this.feedId = feedId;
    this.articles = articles;
  }

  /**
   * Gets the title of this article.
   *
   * @return article title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title of this article.
   *
   * @param title article title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the subtitle of this article, or <code>null</code> if none should be
   * used.
   *
   * @return article subtitle
   */
  public String getSubtitle() {
    return subtitle;
  }

  /**
   * Sets the subtitle of this article, or <code>null</code> if none should be
   * used.
   *
   * @param subtitle article subtitle
   */
  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  /**
   * Gets the URL of this feed.
   *
   * @return feed URL
   */
  public URL getFeedUrl() {
    return feedUrl;
  }

  /**
   * Sets the URL of this feed.
   *
   * @param feedUrl feed URL
   */
  public void setFeedUrl(URL feedUrl) {
    this.feedUrl = feedUrl;
  }

  /**
   * Gets the alternate URL for this feed.
   *
   * @return alternate URL
   */
  public URL getAltUrl() {
    return altUrl;
  }

  /**
   * Sets the alternate URL for this feed.
   *
   * @param altUrl alternate URL
   */
  public void setAltUrl(URL altUrl) {
    this.altUrl = altUrl;
  }

  /**
   * Gets the unique identifier for this feed.
   *
   * @return feed id
   */
  public String getFeedId() {
    return feedId;
  }

  /**
   * Sets the unique identifier for this feed.
   *
   * @param feedId feed id
   */
  public void setFeedId(String feedId) {
    this.feedId = feedId;
  }

  /**
   * Gets the list of articles which should be rendered in this feed.
   *
   * @return article list
   */
  public List<Article> getArticles() {
    return articles;
  }

  /**
   * Sets the list of articles which should be rendered in this feed.
   *
   * @param articles article list
   */
  public void setArticles(List<Article> articles) {
    this.articles = articles;
  }
}
