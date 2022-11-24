package org.randomcoder.content;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InvalidContentTypeExceptionTest {
    @Test
    public void testInvalidContentTypeException() {
        try {
            throw new InvalidContentTypeException("error");
        } catch (InvalidContentTypeException e) {
            assertEquals("error", e.getMessage());
        }
    }
}
