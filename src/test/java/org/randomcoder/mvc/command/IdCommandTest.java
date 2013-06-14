package org.randomcoder.mvc.command;

import static org.junit.Assert.*;

import org.junit.*;

@SuppressWarnings("javadoc")
public class IdCommandTest
{
	private IdCommand c;

	@Before
	public void setUp()
	{
		c = new IdCommand();
	}

	@After
	public void tearDown()
	{
		c = null;
	}

	@Test
	public void testId()
	{
		assertNull(c.getId());
		c.setId(Long.valueOf(1L));
		assertEquals(Long.valueOf(1L), c.getId());
	}
}
