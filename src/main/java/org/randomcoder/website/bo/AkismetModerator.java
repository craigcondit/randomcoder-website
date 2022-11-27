package org.randomcoder.website.bo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.glassfish.hk2.api.Immediate;
import org.randomcoder.website.Config;
import org.randomcoder.website.data.Comment;
import org.randomcoder.website.data.ModerationException;
import org.randomcoder.website.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;

@Immediate
public class AkismetModerator implements Moderator {

    private static final Logger logger = LoggerFactory.getLogger(AkismetModerator.class);

    private final String apiKey;
    private final String siteUrl;
    private final String userAgent;
    private MultiThreadedHttpConnectionManager connectionManager;

    @Inject
    public AkismetModerator(
            @Named(Config.AKISMET_SITE_KEY) String apiKey,
            @Named(Config.AKISMET_SITE_URL) String siteUrl,
            AppInfoBusiness appInfoBusiness) {
        this.apiKey = apiKey;
        this.siteUrl = siteUrl;

        String appName = appInfoBusiness.getApplicationName();
        String appVersion = appInfoBusiness.getApplicationVersion();

        appName = appName.replaceAll("\\s+", "-");
        appVersion = appVersion.replaceAll("\\s+", "_");

        this.userAgent = appName + "/" + appVersion + " | " + getClass().getCanonicalName() + "/" + appVersion;

        logger.info("Using Akismet user agent: " + userAgent);
    }

    @PostConstruct
    public void postConstruct() {
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setStaleCheckingEnabled(true);

        logger.info("Logging into Akismet...");
        if (isAkismetAccountValid()) {
            logger.info("Akismet login successful");
        } else {
            logger.warn("Akismet login failed... Check api key and site url.");
        }
    }

    protected String getVerifyKeyUrl() {
        return "http://rest.akismet.com/1.1/verify-key";
    }

    protected String getCommentCheckUrl() {
        return "http://" + apiKey + ".rest.akismet.com/1.1/comment-check";
    }

    protected String getSubmitSpamUrl() {
        return "http://" + apiKey + ".rest.akismet.com/1.1/submit-spam";
    }

    protected String getSubmitHamUrl() {
        return "http://" + apiKey + ".rest.akismet.com/1.1/submit-ham";
    }

    @PreDestroy
    public void preDestroy() {
        connectionManager.shutdown();
    }

    @Override
    public boolean validate(Comment comment) throws ModerationException {
        PostMethod post = null;
        try {
            HttpClient client = new HttpClient(connectionManager);
            post = new PostMethod(getCommentCheckUrl());
            populatePost(post, comment);
            int status = client.executeMethod(post);
            if (status != HttpStatus.SC_OK) {
                throw new ModerationException("Unknown status code from validation service:" + status);
            }

            String body = post.getResponseBodyAsString().toLowerCase(Locale.US);

            return "false".equals(body);
        } catch (IOException e) {
            throw new ModerationException("Unable to communicate with validation service", e);
        } finally {
            if (post != null) {
                try {
                    post.releaseConnection();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void markAsHam(Comment comment) throws ModerationException {
        PostMethod post = null;
        try {
            HttpClient client = new HttpClient(connectionManager);
            post = new PostMethod(getSubmitHamUrl());
            populatePost(post, comment);
            int status = client.executeMethod(post);
            if (status != HttpStatus.SC_OK) {
                throw new ModerationException("Unknown status code from validation service:" + status);
            }
        } catch (IOException e) {
            throw new ModerationException("Unable to communicate with validation service", e);
        } finally {
            if (post != null) {
                try {
                    post.releaseConnection();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void markAsSpam(Comment comment) throws ModerationException {
        PostMethod post = null;
        try {
            HttpClient client = new HttpClient(connectionManager);
            post = new PostMethod(getSubmitSpamUrl());
            populatePost(post, comment);
            int status = client.executeMethod(post);
            if (status != HttpStatus.SC_OK) {
                throw new ModerationException("Unknown status code from validation service:" + status);
            }
        } catch (IOException e) {
            throw new ModerationException("Unable to communicate with validation service", e);
        } finally {
            if (post != null) {
                try {
                    post.releaseConnection();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void populatePost(PostMethod post, Comment comment) {
        String ip = comment.getIpAddress();
        String ua = comment.getUserAgent();
        String ref = comment.getReferrer();
        User user = comment.getCreatedByUser();
        String author = (user == null) ? comment.getAnonymousUserName() : user.getUserName();
        String email = (user == null) ? comment.getAnonymousEmailAddress() : user.getEmailAddress();
        String url = (user == null) ? comment.getAnonymousWebsite() : user.getWebsite();

        post.setRequestHeader("User-Agent", userAgent);
        post.addParameter("blog", siteUrl);
        post.addParameter("user_ip", (ip == null) ? "" : ip);
        post.addParameter("user_agent", (ua == null) ? "" : ua);
        post.addParameter("referrer", (ref == null) ? "" : ref);
        post.addParameter("permalink", siteUrl + comment.getArticle().getPermalinkUrl());
        post.addParameter("comment_type", "comment");
        if (author != null) post.addParameter("comment_author", author);
        if (email != null) post.addParameter("comment_author_email", email);
        if (url != null) post.addParameter("comment_author_url", url);
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

            if (client.executeMethod(post) != HttpStatus.SC_OK) return false;

            String body = post.getResponseBodyAsString().toLowerCase(Locale.US);

            return "valid".equals(body);
        } catch (Exception e) {
            logger.error("Caught exception", e);
            return false;
        } finally {
            if (post != null) {
                try {
                    post.releaseConnection();
                } catch (Exception ignored) {
                }
            }
        }
    }

}
