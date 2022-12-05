package org.randomcoder.website.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RoleNotFoundExceptionTest {

    @Test
    public void testRoleNotFoundException() {
        try {
            throw new RoleNotFoundException();
        } catch (RoleNotFoundException e) {
            assertNull(e.getMessage());
        }
    }

    @Test
    public void testRoleNotFoundExceptionString() {
        try {
            throw new RoleNotFoundException("test-message");
        } catch (RoleNotFoundException e) {
            assertEquals("test-message", e.getMessage());
        }
    }

}
