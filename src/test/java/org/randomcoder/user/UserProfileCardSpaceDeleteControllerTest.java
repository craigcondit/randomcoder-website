package com.randomcoder.user;

import java.util.*;

import junit.framework.TestCase;

import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.randomcoder.springmvc.IdCommand;
import com.randomcoder.test.mock.dao.*;
import com.randomcoder.test.mock.jse.PrincipalMock;

public class UserProfileCardSpaceDeleteControllerTest extends TestCase
{
	private UserProfileCardSpaceDeleteController controller;
	private UserDaoMock userDao;
	private CardSpaceTokenDaoMock cardSpaceTokenDao;
	private UserBusinessImpl userBusiness;
	
	@Override
	protected void setUp() throws Exception
	{
		userDao = new UserDaoMock();
		cardSpaceTokenDao = new CardSpaceTokenDaoMock();
		userBusiness = new UserBusinessImpl();
		userBusiness.setUserDao(userDao);
		userBusiness.setCardSpaceTokenDao(cardSpaceTokenDao);
		controller = new UserProfileCardSpaceDeleteController();
		controller.setViewName("test");
		controller.setUserBusiness(userBusiness);
	}

	@Override
	protected void tearDown() throws Exception
	{
		controller = null;
		userBusiness = null;
		cardSpaceTokenDao = null;
		userDao = null;
	}

	public void testHandle()
	{
		User user = new User();
		user.setUserName("test");
		user.setEmailAddress("test@example.com");
		user.setEnabled(true);
		user.setRoles(new ArrayList<Role>());
		userDao.create(user);
		
		CardSpaceToken token = new CardSpaceToken();
		token.setCreationDate(new Date());
		token.setEmailAddress("test@example.com");
		token.setPrivatePersonalIdentifier("ppid");
		token.setIssuerHash("issuerHash");
		token.setUser(user);
		Long id = cardSpaceTokenDao.create(token);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setUserPrincipal(new PrincipalMock("test"));
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		IdCommand command = new IdCommand();
		command.setId(id);
		
		BindException errors = new BindException(new Object(), "test");

		assertNotNull("Token not found", cardSpaceTokenDao.findByPrivatePersonalIdentifier("ppid", "issuerHash"));
		
		ModelAndView mav = controller.handle(request, response, command, errors);
		assertNotNull("Null mav", mav);
		assertEquals("Wront view", "test", mav.getViewName());
		assertNull("Token not found", cardSpaceTokenDao.findByPrivatePersonalIdentifier("ppid", "issuerHash"));		
	}
}