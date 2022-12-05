package org.randomcoder.website.jaxrs.resources;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.randomcoder.website.bo.ArticleBusiness;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.feed.FeedException;
import org.randomcoder.website.feed.FeedGenerator;
import org.randomcoder.website.feed.FeedInfo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Singleton
@Path("/feeds")
public class FeedResource {

    private static final int ARTICLE_LIMIT = 20;
    private static final String FEED_TITLE = "randomCoder";
    private static final String FEED_SUBTITLE = "// TODO build a better web";

    private static final URL ATOM_ALL_URL;
    private static final URL RSS20_ALL_URL;
    private static final URL ALT_URL;

    static {
        try {
            ATOM_ALL_URL = new URL("https://randomcoder.org/feeds/atom/all");
            RSS20_ALL_URL = new URL("https://randomcoder.org/feeds/rss20/all");
            ALT_URL = new URL("https://randomcoder.org/");
        } catch (MalformedURLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Inject
    @Named("atomFeedGenerator")
    FeedGenerator atomFeedGenerator;

    @Inject
    @Named("rss20FeedGenerator")
    FeedGenerator rss20FeedGenerator;

    @Inject
    ArticleBusiness articleBusiness;

    @GET
    @Path("atom/all")
    public Response atomAllFeed() throws Exception {
        return generateFeed(atomFeedGenerator, "atom-all", ATOM_ALL_URL);
    }

    @GET
    @Path("rss20/all")
    public Response rss20AllFeed() throws Exception {
        return generateFeed(rss20FeedGenerator, "rss20-all", RSS20_ALL_URL);
    }

    private FeedInfo getFeed(String feedId, URL feedUrl) {
        List<Article> articles = articleBusiness.listRecentArticles(ARTICLE_LIMIT);

        FeedInfo feedInfo = new FeedInfo();

        feedInfo.setFeedUrl(feedUrl);
        feedInfo.setAltUrl(ALT_URL);
        feedInfo.setFeedId(feedId);
        feedInfo.setTitle(FEED_TITLE);
        feedInfo.setSubtitle(FEED_SUBTITLE);
        feedInfo.setArticles(articles);

        return feedInfo;
    }

    private Response generateFeed(FeedGenerator feedGenerator, String feedId, URL feedUrl) throws FeedException, IOException {
        // get feed data
        FeedInfo feedInfo = getFeed(feedId, feedUrl);

        // generate feed
        String feed = feedGenerator.generateFeed(feedInfo);

        // output feed
        byte[] data = feed.getBytes(StandardCharsets.UTF_8);


        return Response.ok(data, feedGenerator.getContentType()).build();
    }

}
