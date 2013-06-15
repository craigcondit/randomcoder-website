package org.randomcoder.db;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Tag repository.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, TagRepositoryCustom
{
	/**
	 * Finds a given {@code Tag} by name.
	 * 
	 * @param name
	 *          tag name
	 * @return {@code Tag} instance, or null if not found
	 */
	@Query("from Tag t where t.name = ?1")
	public Tag findByName(String name);
}
