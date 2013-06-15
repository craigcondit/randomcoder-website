package org.randomcoder.db;

import java.util.*;

import javax.persistence.*;
import javax.persistence.criteria.*;

import org.randomcoder.tag.TagStatistics;
import org.springframework.data.domain.*;

/**
 * Implementation of custom TagRepository methods.
 */
public class TagRepositoryImpl implements TagRepositoryCustom
{
	private static final String QUERY_ALL_TAG_STATISTICS = "select t, t.articles.size from Tag t order by t.displayName";
	
	private EntityManager entityManager;

	/**
	 * Sets the entity manager to use.
	 * 
	 * @param entityManager
	 *          entity manager
	 */
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}
	
	@Override
	public Page<TagStatistics> findAllTagStatistics(Pageable pageable)
	{		
		int start = pageable.getOffset();
		int limit = pageable.getPageSize();

		// retrieve a count first
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = qb.createQuery(Long.class);
		cq.select(qb.count(cq.from(Tag.class)));
		
		Long rowCount = entityManager.createQuery(cq).getSingleResult();
		if (rowCount == null)
		{
			rowCount = 0L;
		}

		// get the page of data
		Query query = entityManager.createQuery(QUERY_ALL_TAG_STATISTICS);
		if (start > 0)
		{
			query.setFirstResult(start);
		}
		if (limit > 0)
		{
			query.setMaxResults(limit);
		}
		List<?> results = query.getResultList();

		List<TagStatistics> tagStats = new ArrayList<TagStatistics>(results.size());

		for (Object result : results)
		{
			Object[] data = (Object[]) result;

			Tag tag = (Tag) data[0];
			int articleCount = ((Number) data[1]).intValue();

			tagStats.add(new TagStatistics(tag, articleCount));
		}

		PageImpl<TagStatistics> page = new PageImpl<>(tagStats, pageable, rowCount);
		
		return page;
	}
}
