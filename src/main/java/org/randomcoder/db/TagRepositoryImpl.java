package org.randomcoder.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.randomcoder.tag.TagStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Implementation of custom TagRepository methods.
 */
public class TagRepositoryImpl implements TagRepositoryCustom {
	private static final String QUERY_ALL_TAG_STATISTICS = "select t, size(t.articles) from Tag t group by t order by t.displayName";
	private static final String NATIVE_QUERY_MOST_ARTICLES = "select max(c) from (select article_id, count(1) c from article_tag_link group by article_id) x";

	private EntityManager entityManager;

	/**
	 * Sets the entity manager to use.
	 * 
	 * @param entityManager
	 *            entity manager
	 */
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<TagStatistics> findAllTagStatistics() {
		Query query = entityManager.createQuery(QUERY_ALL_TAG_STATISTICS);
		return buildTagStatistics(query);
	}

	@Override
	public Page<TagStatistics> findAllTagStatistics(Pageable pageable) {
		int start = (int) pageable.getOffset();
		int limit = pageable.getPageSize();

		// retrieve a count first
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = qb.createQuery(Long.class);
		cq.select(qb.count(cq.from(Tag.class)));

		Long rowCount = entityManager.createQuery(cq).getSingleResult();
		if (rowCount == null) {
			rowCount = 0L;
		}

		// get the page of data
		Query query = entityManager.createQuery(QUERY_ALL_TAG_STATISTICS);
		if (start > 0) {
			query.setFirstResult(start);
		}
		if (limit > 0) {
			query.setMaxResults(limit);
		}

		List<TagStatistics> tagStats = buildTagStatistics(query);
		PageImpl<TagStatistics> page = new PageImpl<>(tagStats, pageable, rowCount);

		return page;
	}

	@Override
	public int maxArticleCount() {
		Number result = (Number) entityManager.createNativeQuery(NATIVE_QUERY_MOST_ARTICLES).getSingleResult();
		return result == null ? 0 : result.intValue();
	}

	private List<TagStatistics> buildTagStatistics(Query query) {
		List<?> results = query.getResultList();

		List<TagStatistics> tagStats = new ArrayList<>(results.size());

		for (Object result : results) {
			Object[] data = (Object[]) result;

			Tag tag = (Tag) data[0];
			int articleCount = ((Number) data[1]).intValue();

			tagStats.add(new TagStatistics(tag, articleCount));
		}
		return tagStats;
	}
}
