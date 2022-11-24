package org.randomcoder.article.moderation;

import org.randomcoder.db.Comment;

/**
 * Interface for generic moderator plugins.
 */
public interface Moderator {
    /**
     * Validates the given comment as spam or ham.
     *
     * @param comment comment to validate
     * @return true if comment is legitimate, false if comment is spam
     * @throws ModerationException if operation could not be performed
     */
    boolean validate(Comment comment) throws ModerationException;

    /**
     * Marks the given comment as spam.
     *
     * @param comment comment to mark as spam
     * @throws ModerationException if operation could not be performed
     */
    void markAsSpam(Comment comment) throws ModerationException;

    /**
     * Marks the given comment as ham (non-spam).
     *
     * @param comment comment to mark as ham
     * @throws ModerationException if operation could not be performed
     */
    void markAsHam(Comment comment) throws ModerationException;
}
