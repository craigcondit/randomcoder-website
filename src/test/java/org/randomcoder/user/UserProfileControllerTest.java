package org.randomcoder.user;

import static org.easymock.EasyMock.*;

import java.util.Map;

import junit.framework.TestCase;

import org.easymock.IMocksControl;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.UserDao;
import org.randomcoder.test.mock.jse.PrincipalMock;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings("javadoc")
public class UserProfileControllerTest extends TestCase
{
	private UserProfileController controller;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private UserDao userDao;
	private UserBusiness userBusiness;
	private IMocksControl control;
	
	@Override
	protected void setUp() throws Exception
	{		
		control = createControl();
		
		request = new MockHttpServletRequest();
		request.setUserPrincipal(new PrincipalMock("test"));
		
		response = new MockHttpServletResponse();
		
		controller = new UserProfileController();

		userDao = control.createMock(UserDao.class);
		userBusiness = control.createMock(UserBusiness.class);
		
		controller.setUserDao(userDao);
		controller.setUserBusiness(userBusiness);
		controller.setSuccessView("success");
	}

	@Override
	protected void tearDown() throws Exception
	{		
		userDao = null;
		control = null;
		controller = null;
		response = null;
		request = null;
	}

	public void testInitBinder() throws Exception
	{
		ServletRequestDataBinder binder = new ServletRequestDataBinder(new Object());
	
		controller.initBinder(request, binder);
	}

	public void testReferenceData() throws Exception
	{
		UserProfileCommand command = new UserProfileCommand();
		BindException errors = new BindException(command, "test");

		User user = new User();
		user.setId(1L);
		user.setEnabled(true);
		user.setUserName("test");
		
		expect(userDao.findByUserName("test")).andReturn(user);
		control.replay();
		
		Map<String, Object> data = controller.referenceData(request, command, errors);
		control.verify();
		
		assertNotNull("Null data", data);
		assertSame("Wrong user", user, data.get("user"));
	}

	public void testReferenceDataUserNotFound() throws Exception
	{
		UserProfileCommand command = new UserProfileCommand();
		BindException errors = new BindException(command, "test");

		expect(userDao.findByUserName("test")).andReturn(null);
		control.replay();
		
		try
		{
			controller.referenceData(request, command, errors);
			fail("Exception not thrown");
		}
		catch (UserNotFoundException e)
		{
			// pass
		}
		control.verify();
	}

	public void testOnBindOnNewForm() throws Exception
	{
		UserProfileCommand command = new UserProfileCommand();
		BindException errors = new BindException(command, "test");
		
		User user = new User();
		user.setId(1L);
		user.setEnabled(true);
		user.setUserName("test");
		user.setEmailAddress("test@example.com");
		user.setWebsite("http://www.test.com/");
		
		expect(userDao.findByUserName("test")).andReturn(user);
		control.replay();
		
		controller.onBindOnNewForm(request, command, errors);
		control.verify();
		assertEquals("Wrong username", "test@example.com", command.getEmailAddress());
		assertEquals("Wrong website", "http://www.test.com/", command.getWebsite());
	}

	public void testOnSubmitPrefs() throws Exception
	{
		UserProfileCommand command = new UserProfileCommand();
		
		BindException errors = new BindException(command, "test");
		
		User user = new User();
		user.setId(1L);
		user.setEnabled(true);
		user.setUserName("test");
		user.setEmailAddress("test@example.com");
		user.setWebsite("http://www.test.com/");
		
		expect(userDao.findByUserName("test")).andReturn(user);
		userBusiness.updateUser(command, 1L);
		control.replay();
		
		ModelAndView mav = controller.onSubmit(request, response, command, errors);
		control.verify();
		assertNotNull("Null MAV", mav);
		assertEquals("Wrong view", "success", mav.getViewName());
	}
}
