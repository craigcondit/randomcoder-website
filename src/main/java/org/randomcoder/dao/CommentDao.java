package org.randomcoder.dao;

import org.randomcoder.db.Comment;

public interface CommentDao {

    Comment findById(long commentId);

    void deleteById(long commentId);

    Long save(Comment comment);

    Page<Comment> listForModeration(long offset, long length);

}
