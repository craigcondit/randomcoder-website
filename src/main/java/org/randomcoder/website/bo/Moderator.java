package org.randomcoder.website.bo;

import org.randomcoder.website.data.Comment;
import org.randomcoder.website.data.ModerationException;

public interface Moderator {

    boolean validate(Comment comment) throws ModerationException;

    void markAsSpam(Comment comment) throws ModerationException;

    void markAsHam(Comment comment) throws ModerationException;

}
