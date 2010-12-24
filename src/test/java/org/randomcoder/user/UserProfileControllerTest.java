package org.randomcoder.user;

import static org.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_ALL_FIELDS;
import static org.easymock.EasyMock.*;

import java.beans.PropertyEditor;
import java.security.PublicKey;
import java.util.*;

import junit.framework.TestCase;

import org.easymock.IMocksControl;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;

import org.randomcoder.crypto.CertificateContext;
import org.randomcoder.saml.SamlAssertion;
import org.randomcoder.security.cardspace.CardSpaceCredentials;
import org.randomcoder.test.TestObjectFactory;
import org.randomcoder.test.mock.jse.PrincipalMock;

public class UserProfileControllerTest extends TestCase
{
	private UserProfileController controller;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private UserDao userDao;
	private CardSpaceTokenDao cardSpaceTokenDao;
	private UserBusiness userBusiness;
	private IMocksControl control;
	private CertificateContext context;
	
	@Override
	protected void setUp() throws Exception
	{		
		control = createControl();
		
		request = new MockHttpServletRequest();
		request.setUserPrincipal(new PrincipalMock("test"));
		
		response = new MockHttpServletResponse();
		
		controller = new UserProfileController();

		userDao = control.createMock(UserDao.class);
		cardSpaceTokenDao = control.createMock(CardSpaceTokenDao.class);
		userBusiness = control.createMock(UserBusiness.class);
		
		context = TestObjectFactory.getCertificateContext();
		
		controller.setCardSpaceTokenDao(cardSpaceTokenDao);
		controller.setUserDao(userDao);
		controller.setUserBusiness(userBusiness);
		controller.setSuccessView("success");
		controller.setCertificateContext(context);
	}

	@Override
	protected void tearDown() throws Exception
	{		
		context = null;
		cardSpaceTokenDao = null;
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
		PropertyEditor editor = binder.findCustomEditor(CardSpaceCredentials.class, "test");
		assertNotNull("Null property editor", editor);
		assertTrue("Wrong class", editor instanceof CardSpaceCredentialsPropertyEditor);
	}

	public void testReferenceData() throws Exception
	{
		UserProfileCommand command = new UserProfileCommand();
		BindException errors = new BindException(command, "test");

		User user = new User();
		user.setId(1L);
		user.setEnabled(true);
		user.setUserName("test");
		
		List<CardSpaceToken> cards = new ArrayList<CardSpaceToken>();
		
		expect(userDao.findByUserName("test")).andReturn(user);
		expect(cardSpaceTokenDao.listByUser(user)).andReturn(cards);		
		control.replay();
		
		Map<String, Object> data = controller.referenceData(request, command, errors);
		control.verify();
		
		assertNotNull("Null data", data);
		assertSame("Wrong user", user, data.get("user"));
		assertSame("Wrong list", cards, data.get("cardSpaceTokens"));
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
		command.setFormType("PREFS");
		
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
	
	public void testOnSubmitInfocard() throws Exception
	{
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_ALL_FIELDS);
		SamlAssertion assertion = TestObjectFactory.getSamlAssertion(doc);
		PublicKey publicKey = TestObjectFactory.getPublicKey(doc);
		Date now = new Date();
		
		CardSpaceCredentials cred = new CardSpaceCredentials(assertion, publicKey, now);
		
		UserProfileCommand command = new UserProfileCommand();
		command.setFormType("INFOCARD");
		command.setXmlToken(cred);
		
		BindException errors = new BindException(command, "test");
		
		User user = new User();
		user.setId(1L);
		user.setEnabled(true);
		user.setUserName("test");
		user.setEmailAddress("test@example.com");
		user.setWebsite("http://www.test.com/");
		
		expect(userDao.findByUserName("test")).andReturn(user);
		userBusiness.associateCardSpaceCredentials(1L, cred);
		control.replay();
		
		ModelAndView mav = controller.onSubmit(request, response, command, errors);
		control.verify();
		assertNotNull("Null MAV", mav);
		assertEquals("Wrong view", "success", mav.getViewName());
	}
}
