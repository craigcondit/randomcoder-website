package org.randomcoder.db;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Role data access interface.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>
{
	/**
	 * Finds a {@code Role} instance with the given name.
	 * 
	 * @param name
	 *            role name
	 * @return {@code Role} instance, or null if not found
	 */
	@Query("from Role r where r.name = ?")
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	public Role findByName(String name);
}