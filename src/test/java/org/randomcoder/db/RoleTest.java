package org.randomcoder.db;

import org.junit.Test;

import static org.junit.Assert.*;

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

        assertNotEquals(null, role1);
        assertNotEquals(role1, new Object());
        assertNotEquals(role1, role2);
        assertEquals(role1, role3);
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

}
