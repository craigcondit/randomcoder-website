package org.randomcoder.article.moderation;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.randomcoder.bo.AppInfoBusiness;
import org.randomcoder.db.Comment;
import org.randomcoder.db.CommentIp;
import org.randomcoder.db.CommentReferrer;
import org.randomcoder.db.CommentUserAgent;
import org.randomcoder.db.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.util.Locale;

/**
 * Moderator implementation which queries Akismet.
 */
public class AkismetModerator
    implements Moderator, InitializingBean, DisposableBean {
  private static final Logger logger =
      LoggerFactory.getLogger(AkismetModerator.class);

  private MultiThreadedHttpConnectionManager connectionManager;

  private final String apiKey;
  private final String siteUrl;
  private final String userAgent;

  /**
   * Creates a new moderator.
   *
   * @param apiKey API key
   * @param siteUrl site url
   * @param appInfoBusiness application information
   */
  public AkismetModerator(String apiKey, String siteUrl, AppInfoBusiness appInfoBusiness) {
    this.apiKey = apiKey;
    this.siteUrl = siteUrl;

    String appName = appInfoBusiness.getApplicationName();
    String appVersion = appInfoBusiness.getApplicationVersion();

    appName = appName.replaceAll("\\s+", "-");
    appVersion = appVersion.replaceAll("\\s+", "_");

    this.userAgent =
        appName + "/" + appVersion + " | " + getClass().getCanonicalName() + "/"
            + appVersion;

    logger.info("Using Akismet user agent: " + userAgent);
  }

  /**
   * Initializes the Akismet filter.
   *
   * @throws Exception if an error occurs
   */
  @Override public void afterPropertiesSet() throws Exception {
    connectionManager = new MultiThreadedHttpConnectionManager();
    connectionManager.getParams().setStaleCheckingEnabled(true);

    logger.info("Logging into Akismet...");
    if (isAkismetAccountValid()) {
      logger.info("Akismet login successful");
    } else {
      logger.warn("Akismet login failed... Check api key and site url.");
    }
  }

  /**
   * Gets the Akismet URL to use for account verification.
   *
   * @return verification url
   */
  protected String getVerifyKeyUrl() {
    return "http://rest.akismet.com/1.1/verify-key";
  }

  /**
   * Gets the Akismet URL to use for comment checking.
   *
   * @return comment check url
   */
  protected String getCommentCheckUrl() {
    return "http://" + apiKey + ".rest.akismet.com/1.1/comment-check";
  }

  /**
   * Gets the Akismet URL to use for spam submission.
   *
   * @return submit spam url
   */
  protected String getSubmitSpamUrl() {
    return "http://" + apiKey + ".rest.akismet.com/1.1/submit-spam";
  }

  /**
   * Gets the Akismet URL to use for ham submission.
   *
   * @return submit ham url
   */
  protected String getSubmitHamUrl() {
    return "http://" + apiKey + ".rest.akismet.com/1.1/submit-ham";
  }

  /**
   * Shuts down the Akismet filter.
   *
   * @throws Exception if an error occurs
   */
  @Override public void destroy() throws Exception {
    connectionManager.shutdown();
  }

  @Override public boolean validate(Comment comment)
      throws ModerationException {
    PostMethod post = null;
    try {
      HttpClient client = new HttpClient(connectionManager);
      post = new PostMethod(getCommentCheckUrl());
      populatePost(post, comment);
      int status = client.executeMethod(post);
      if (status != HttpStatus.SC_OK) {
        throw new ModerationException(
            "Unknown status code from validation service:" + status);
      }

      String body = post.getResponseBodyAsString().toLowerCase(Locale.US);

      return "false".equals(body);
    } catch (IOException e) {
      throw new ModerationException(
          "Unable to communicate with validation service", e);
    } finally {
      if (post != null)
        try {
          post.releaseConnection();
        } catch (Exception ignored) {
        }
    }
  }

  @Override public void markAsHam(Comment comment) throws ModerationException {
    PostMethod post = null;
    try {
      HttpClient client = new HttpClient(connectionManager);
      post = new PostMethod(getSubmitHamUrl());
      populatePost(post, comment);
      int status = client.executeMethod(post);
      if (status != HttpStatus.SC_OK) {
        throw new ModerationException(
            "Unknown status code from validation service:" + status);
      }
    } catch (IOException e) {
      throw new ModerationException(
          "Unable to communicate with validation service", e);
    } finally {
      if (post != null)
        try {
          post.releaseConnection();
        } catch (Exception ignored) {
        }
    }
  }

  @Override public void markAsSpam(Comment comment) throws ModerationException {
    PostMethod post = null;
    try {
      HttpClient client = new HttpClient(connectionManager);
      post = new PostMethod(getSubmitSpamUrl());
      populatePost(post, comment);
      int status = client.executeMethod(post);
      if (status != HttpStatus.SC_OK) {
        throw new ModerationException(
            "Unknown status code from validation service:" + status);
      }
    } catch (IOException e) {
      throw new ModerationException(
          "Unable to communicate with validation service", e);
    } finally {
      if (post != null)
        try {
          post.releaseConnection();
        } catch (Exception ignored) {
        }
    }
  }

  private void populatePost(PostMethod post, Comment comment) {
    CommentIp ip = comment.getIpAddress();
    CommentUserAgent ua = comment.getUserAgent();
    CommentReferrer ref = comment.getReferrer();
    User user = comment.getCreatedByUser();
    String author =
        (user == null) ? comment.getAnonymousUserName() : user.getUserName();
    String email = (user == null) ? comment.getAnonymousEmailAddress() :
        user.getEmailAddress();
    String url =
        (user == null) ? comment.getAnonymousWebsite() : user.getWebsite();

    post.setRequestHeader("User-Agent", userAgent);
    post.addParameter("blog", siteUrl);
    post.addParameter("user_ip", (ip == null) ? "" : ip.getIpAddress());
    post.addParameter("user_agent", (ua == null) ? "" : ua.getUserAgentName());
    post.addParameter("referrer", (ref == null) ? "" : ref.getReferrerUri());
    post.addParameter("permalink",
        siteUrl + comment.getArticle().getPermalinkUrl());
    post.addParameter("comment_type", "comment");
    if (author != null)
      post.addParameter("comment_author", author);
    if (email != null)
      post.addParameter("comment_author_email", email);
    if (url != null)
      post.addParameter("comment_author_url", url);
    post.addParameter("comment_content", comment.getContent());
  }

  private boolean isAkismetAccountValid() {
    PostMethod post = null;
    try {
      HttpClient client = new HttpClient(connectionManager);

      post = new PostMethod(getVerifyKeyUrl());
      post.setRequestHeader("User-Agent", userAgent);
      post.addParameter("key", apiKey);
      post.addParameter("blog", siteUrl);

      if (client.executeMethod(post) != HttpStatus.SC_OK)
        return false;

      String body = post.getResponseBodyAsString().toLowerCase(Locale.US);

      return "valid".equals(body);
    } catch (Exception e) {
      logger.error("Caught exception", e);
      return false;
    } finally {
      if (post != null)
        try {
          post.releaseConnection();
        } catch (Exception ignored) {
        }
    }
  }
}
