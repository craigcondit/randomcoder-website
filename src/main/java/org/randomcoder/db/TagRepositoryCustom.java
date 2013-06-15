package org.randomcoder.db;

import org.randomcoder.tag.TagStatistics;
import org.springframework.data.domain.*;

/**
 * Custom methods added to {@link TagRepository}.
 */
public interface TagRepositoryCustom
{
	/**
	 * Retrieves a page of Tag statistics.
	 * 
	 * @param pageable
	 *          pager
	 * @return Page of TagStatistics
	 */
	public Page<TagStatistics> findAllTagStatistics(Pageable pageable);
}
