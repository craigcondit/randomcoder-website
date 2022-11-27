package org.randomcoder.website.dao;

import org.randomcoder.website.data.Comment;
import org.randomcoder.website.data.Page;

public interface CommentDao {

    Comment findById(long commentId);

    void deleteById(long commentId);

    Long save(Comment comment);

    Page<Comment> listForModeration(long offset, long length);

}
