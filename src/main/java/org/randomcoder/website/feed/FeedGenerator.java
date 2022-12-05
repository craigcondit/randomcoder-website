package org.randomcoder.website.feed;

public interface FeedGenerator {

    String generateFeed(FeedInfo info) throws FeedException;

    String getContentType();

}
