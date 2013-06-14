package org.randomcoder.bo;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.*;
import org.randomcoder.download.cache.CachingPackageListProducer;

@SuppressWarnings("javadoc")
public class ScheduledTasksTest
{
	private IMocksControl control;
	private ArticleBusiness ab;
	private CachingPackageListProducer cmr;
	private ScheduledTasks st;

	@Before
	public void setUp()
	{
		control = createControl();
		ab = control.createMock(ArticleBusiness.class);
		cmr = control.createMock(CachingPackageListProducer.class);
		st = new ScheduledTasks();
		st.setModerationBatchSize(3);
		st.setArticleBusiness(ab);
		st.setCachingMavenRepository(cmr);
	}

	@After
	public void tearDown()
	{
		st = null;
		ab = null;
		control = null;

	}

	@Test
	public void testModerateComments() throws Exception
	{
		expect(ab.moderateComments(3)).andReturn(true);
		expect(ab.moderateComments(3)).andReturn(false);
		control.replay();
		
		st.moderateComments();
		control.verify();
	}
	
	@Test
	public void testModerateCommentsError() throws Exception
	{
		expect(ab.moderateComments(3)).andThrow(new RuntimeException("test"));
		control.replay();
		
		st.moderateComments();
		control.verify();
	}
	
	@Test
	public void testRefreshMavenRepository() throws Exception
	{
		cmr.refresh();
		control.replay();
		
		st.refreshMavenRepository();
		control.verify();
	}
	
	@Test
	public void testRefreshMavenRepositoryError() throws Exception
	{
		cmr.refresh();
		expectLastCall().andThrow(new RuntimeException("test"));
		control.replay();
		
		st.refreshMavenRepository();
		control.verify();
	}
}
