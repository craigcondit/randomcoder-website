package org.randomcoder.db;

import java.util.Iterator;

import org.randomcoder.article.comment.Comment;
import org.randomcoder.dao.CrudDao;

/**
 * Comment data access interface.
 */
public interface CommentDao extends CrudDao<Comment, Long>
{
	/**
	 * Iterates through all comments which are available to be moderated.
	 * 
	 * @param start
	 *          starting index
	 * @param limit
	 *          maximum number of results to return
	 * @return Comment Iterator.
	 */
	public Iterator<Comment> iterateForModerationInRange(int start, int limit);
}
