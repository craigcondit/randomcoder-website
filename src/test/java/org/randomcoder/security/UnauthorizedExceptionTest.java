package org.randomcoder.security;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UnauthorizedExceptionTest {
    @Test
    public void testUnauthorizedException() {
        try {
            throw new UnauthorizedException();
        } catch (UnauthorizedException e) {
            assertNull(e.getMessage());
        }
    }

    @Test
    public void testUnauthorizedExceptionString() {
        try {
            throw new UnauthorizedException("test-message");
        } catch (UnauthorizedException e) {
            assertEquals("test-message", e.getMessage());
        }
    }
}
