package org.randomcoder.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class RoleTest {
	@Test
	public void testEqualsObject() {
		Role role1 = new Role();
		role1.setId((long) 1);
		role1.setName("Role 1");
		role1.setDescription("B");

		Role role2 = new Role();
		role2.setId((long) 2);
		role2.setName("Role 2");
		role2.setDescription("A");

		Role role3 = new Role();
		role3.setId((long) 3);
		role3.setName("Role 1");
		role3.setDescription("C");

		assertFalse(role1.equals(null));
		assertFalse(role1.equals(new Object()));
		assertFalse(role1.equals(role2));
		assertTrue(role1.equals(role3));
	}

	@Test
	public void testCompareTo() {
		Role role1 = new Role();
		role1.setId((long) 1);
		role1.setName("Role 1");
		role1.setDescription("B");

		Role role2 = new Role();
		role2.setId((long) 2);
		role2.setName("Role 2");
		role2.setDescription("A");

		assertEquals(1, role1.compareTo(role2));
	}

	@Test
	public void testNameComparator() {
		Role role1 = new Role();
		role1.setId((long) 1);
		role1.setName("Role 1");

		Role role2 = new Role();
		role2.setId((long) 2);
		role2.setName("Role 2");

		assertEquals(-1, Role.NAME_COMPARATOR.compare(role1, role2));
	}
}
