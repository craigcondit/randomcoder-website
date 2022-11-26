package org.randomcoder.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Article repository.
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * Lists {@code Article} objects created within the specified date range.
     *
     * @param startDate lower bound of date range (inclusive)
     * @param endDate   upper bound of date range (exclusive)
     * @return list of {@code Article} objects
     */
    @Query("from Article a where a.creationDate >= ?1 and a.creationDate < ?2 order by a.creationDate desc")
    List<Article> findBetweenDates(Date startDate, Date endDate);

    /**
     * Lists {@code Article} objects created within the specified date range.
     *
     * @param tag       tag to restrict by
     * @param startDate lower bound of date range (inclusive)
     * @param endDate   upper bound of date range (exclusive)
     * @return list of {@code Article} objects
     */
    @Query("from Article a where ?1 in elements(a.tags) and a.creationDate >= ?2 and a.creationDate < ?3 order by a.creationDate desc")
    List<Article> findByTagBetweenDates(Tag tag, Date startDate,
                                        Date endDate);
}