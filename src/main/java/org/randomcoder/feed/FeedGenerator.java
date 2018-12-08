package org.randomcoder.feed;

/**
 * Feed generator interface. A feed generator is responsible for generating a
 * syndicated feed from a group of articles.
 */
public interface FeedGenerator {
  /**
   * Generates a feed from the given list of articles.
   *
   * @param info feed info
   * @return string representation of feed
   * @throws FeedException if an error occurs during feed generation
   */
  public String generateFeed(FeedInfo info) throws FeedException;

  /**
   * Gets the content (mime) type of generated feeds.
   *
   * @return mime type
   */
  public String getContentType();
}
