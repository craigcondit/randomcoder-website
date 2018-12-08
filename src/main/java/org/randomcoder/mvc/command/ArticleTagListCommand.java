package org.randomcoder.mvc.command;

import org.randomcoder.db.Tag;

/**
 * Command object used for paged tag queries.
 */
public class ArticleTagListCommand extends ArticleListCommand {
  private static final long serialVersionUID = 3214610458593305928L;

  private Tag tag;

  /**
   * Gets the tag associated with this command.
   *
   * @return tag
   */
  public Tag getTag() {
    return tag;
  }

  /**
   * Sets the tag associated with this command.
   *
   * @param tag tag
   */
  public void setTag(Tag tag) {
    this.tag = tag;
  }
}
