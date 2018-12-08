package org.randomcoder.mvc.command;

import org.apache.commons.lang.StringUtils;
import org.randomcoder.content.ContentType;
import org.randomcoder.db.Comment;
import org.randomcoder.io.Producer;

import java.io.Serializable;

/**
 * Command class for comment posting.
 */
public class CommentCommand implements Serializable, Producer<Comment> {
  private static final long serialVersionUID = -1245687879900306444L;

  private boolean anonymous;

  private String anonymousUserName;
  private String anonymousEmailAddress;
  private String anonymousWebsite;

  private String title;
  private String content;

  /**
   * Determines if this comment is anonymous or not.
   *
   * @return true if anonymous, false otherwise
   */
  public boolean isAnonymous() {
    return anonymous;
  }

  /**
   * Binds non-request parameters to form.
   *
   * @param isAnonymous true if anonymous, false otherwise
   */
  public void bind(boolean isAnonymous) {
    this.anonymous = isAnonymous;
  }

  /**
   * Get the anonymous name of the comment poster.
   *
   * @return anonymous name
   */
  public String getAnonymousUserName() {
    return anonymousUserName;
  }

  /**
   * Set the anonymous name of the comment poster.
   *
   * @param anonymousUserName anonymous name
   */
  public void setAnonymousUserName(String anonymousUserName) {
    this.anonymousUserName = StringUtils.trimToNull(anonymousUserName);
  }

  /**
   * Gets the anonymous email address of the comment poster.
   *
   * @return anonymous email address
   */
  public String getAnonymousEmailAddress() {
    return anonymousEmailAddress;
  }

  /**
   * Sets the anonymous email address of the comment poster.
   *
   * @param anonymousEmailAddress email address
   */
  public void setAnonymousEmailAddress(String anonymousEmailAddress) {
    this.anonymousEmailAddress = StringUtils.trimToNull(anonymousEmailAddress);
  }

  /**
   * Gets the anonymous website associated with this user.
   *
   * @return anonymous web site
   */
  public String getAnonymousWebsite() {
    return anonymousWebsite;
  }

  /**
   * Sets the anonymous website associated with this user.
   *
   * @param anonymousWebsite anonymous website
   */
  public void setAnonymousWebsite(String anonymousWebsite) {
    this.anonymousWebsite = StringUtils.trimToNull(anonymousWebsite);
  }

  /**
   * Gets the title of this comment.
   *
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title of this comment.
   *
   * @param title title
   */
  public void setTitle(String title) {
    this.title = StringUtils.trimToNull(title);
  }

  /**
   * Gets the text of the comment.
   *
   * @return comment text
   */
  public String getContent() {
    return content;
  }

  /**
   * Sets the text of the comment.
   *
   * @param content comment text
   */
  public void setContent(String content) {
    this.content = StringUtils.trimToNull(content);
  }

  /**
   * Populates a comment object with data.
   */
  @Override public void produce(Comment comment) {
    if (anonymous) {
      comment.setAnonymousUserName(anonymousUserName);
      comment.setAnonymousEmailAddress(anonymousEmailAddress);
      comment.setAnonymousWebsite(anonymousWebsite);
    } else {
      comment.setAnonymousUserName(null);
      comment.setAnonymousEmailAddress(null);
      comment.setAnonymousWebsite(null);
    }

    comment.setTitle(title);
    comment.setContent(content);

    // TODO allow other types
    comment.setContentType(ContentType.TEXT);
  }

}
