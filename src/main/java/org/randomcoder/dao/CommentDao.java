package org.randomcoder.dao;

import org.randomcoder.db.Comment;

import java.util.List;

public interface CommentDao {

    Page<Comment> listForModeration(long offset, long length);

}
