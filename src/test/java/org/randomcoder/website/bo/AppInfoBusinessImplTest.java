package org.randomcoder.website.bo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AppInfoBusinessImplTest {

    private AppInfoBusinessImpl info;

    @Before
    public void setUp() throws Exception {
        info = new AppInfoBusinessImpl();
    }

    @After
    public void tearDown() {
        info = null;
    }

    @Test
    public void testGetObject() {
        assertEquals("Test-Application", info.getApplicationName());
        assertEquals("1.0.0", info.getApplicationVersion());
    }

}