package org.randomcoder.db;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * JPA entity representing a comment referrer.
 */
@Entity @Table(name = "comment_referrers")
@SequenceGenerator(name = "comment_referrers", sequenceName = "comment_referrers_seq", allocationSize = 1)
public class CommentReferrer implements Serializable {
  private static final long serialVersionUID = 4101138502746346499L;

  private Long id;
  private String referrerUri;
  private Date creationDate;

  /**
   * Gets the ID for this referrer.
   *
   * @return id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "comment_referrers")
  @Column(name = "comment_referrer_id") public Long getId() {
    return id;
  }

  /**
   * Sets the ID for this referrer.
   *
   * @param id id
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the URI sent as the HTTP referrer.
   *
   * @return referrer URI
   */
  @Column(name = "referrer", nullable = false, length = 1024)
  public String getReferrerUri() {
    return referrerUri;
  }

  /**
   * Sets the URI sent as the HTTP referrer.
   *
   * @param referrerUri referrer URI
   */
  public void setReferrerUri(String referrerUri) {
    this.referrerUri = referrerUri;
  }

  /**
   * Gets the creation date of this referrer.
   *
   * @return creation date
   */
  @Column(name = "create_date", nullable = false)
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * Sets the creation date of this referrer.
   *
   * @param creationDate creation date
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * Gets the hash code of this referrer.
   *
   * @return hash code
   */
  @Override public int hashCode() {
    return StringUtils.trimToEmpty(getReferrerUri()).hashCode();
  }

  /**
   * Determines if two CommentReferrer objects are equal.
   *
   * @return true if equal, false if not
   */
  @Override public boolean equals(Object obj) {
    if (!(obj instanceof CommentReferrer))
      return false;

    CommentReferrer ref = (CommentReferrer) obj;

    // two referrers are equal if and only if their uris match
    String uri1 = StringUtils.trimToEmpty(getReferrerUri());
    String uri2 = StringUtils.trimToEmpty(ref.getReferrerUri());

    return uri1.equals(uri2);
  }

  /**
   * Gets a string representation of this object, suitable for debugging.
   *
   * @return string representation of this object
   */
  @Override public String toString() {
    return ReflectionToStringBuilder
        .toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
