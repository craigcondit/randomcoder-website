package org.randomcoder.db;

import java.util.List;

import org.randomcoder.tag.TagStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom methods added to {@link TagRepository}.
 */
public interface TagRepositoryCustom {
	/**
	 * Retrieves all Tag statistics.
	 * 
	 * @return Page of TagStatistics
	 */
	public List<TagStatistics> findAllTagStatistics();

	/**
	 * Retrieves a page of Tag statistics.
	 * 
	 * @param pageable
	 *            pager
	 * @return Page of TagStatistics
	 */
	public Page<TagStatistics> findAllTagStatistics(Pageable pageable);

	/**
	 * Returns the maximum number of articles a given tag has.
	 * 
	 * @return max article count
	 */
	public int maxArticleCount();
}
