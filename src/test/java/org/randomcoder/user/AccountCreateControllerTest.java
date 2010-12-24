package com.randomcoder.user;

import java.beans.PropertyEditor;
import java.security.PublicKey;
import java.util.Date;

import junit.framework.TestCase;

import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;

import com.randomcoder.cardspace.CardSpaceTokenSpec;
import com.randomcoder.crypto.*;
import com.randomcoder.saml.SamlAssertion;
import com.randomcoder.security.cardspace.CardSpaceCredentials;
import com.randomcoder.test.TestObjectFactory;
import com.randomcoder.test.mock.dao.*;
import com.randomcoder.test.mock.user.AccountCreateControllerMock;

import static com.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_ALL_FIELDS;

public class AccountCreateControllerTest extends TestCase
{
	private AccountCreateControllerMock controller;
	private UserDaoMock userDao;
	private CardSpaceTokenDaoMock cardSpaceTokenDao;
	private UserBusinessImpl userBusiness;
	private CertificateContext certificateContext;
	private TransientAESEncryptionContext encryptionContext;
	
	@Override
	protected void setUp() throws Exception
	{
		userDao = new UserDaoMock();
		cardSpaceTokenDao = new CardSpaceTokenDaoMock();
		userBusiness = new UserBusinessImpl();
		userBusiness.setUserDao(userDao);
		userBusiness.setCardSpaceTokenDao(cardSpaceTokenDao);
		certificateContext = TestObjectFactory.getCertificateContext();
		encryptionContext = new TransientAESEncryptionContext();
		encryptionContext.setKeySize(128);
		controller = new AccountCreateControllerMock();
		controller.setUserBusiness(userBusiness);
		controller.setCertificateContext(certificateContext);
		controller.setEncryptionContext(encryptionContext);
		controller.setFormView("form");
		controller.setSuccessView("success");
	}

	@Override
	protected void tearDown() throws Exception
	{
		controller = null;
		encryptionContext = null;
		certificateContext = null;
		userBusiness = null;
		cardSpaceTokenDao = null;
		userDao = null;
	}

	public void testInitBinder() throws Exception
	{
		PropertyEditor editor;
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		ServletRequestDataBinder binder = new ServletRequestDataBinder(new Object(), "test");
		controller.initBinder(request, binder);
		
		editor = binder.findCustomEditor(CardSpaceCredentials.class, "xmlToken");
		assertNotNull("No editor for CardSpaceCredentials", editor);
		assertTrue("Wrong class for CardSpaceCredentials property editor", editor instanceof CardSpaceCredentialsPropertyEditor);
		
		editor = binder.findCustomEditor(CardSpaceTokenSpec.class, "xmlToken");
		assertNotNull("No editor for CardSpaceTokenSpec", editor);
		assertTrue("Wrong class for CardSpaceTokenSpec property editor", editor instanceof EncryptedObjectPropertyEditor);
	}

	public void testOnSubmitPassword() throws Exception
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		AccountCreateCommand command = new AccountCreateCommand();
		command.setFormComplete(true);
		command.setFormType("PASS");
		command.setUserName("test");
		command.setEmailAddress("test@example.com");
		command.setPassword("Password1");
		command.setPassword2("Password1");
		command.setWebsite("http://www.example.com/");
		
		ModelAndView mav = controller.onSubmit(request, response, command, new BindException(new Object(), "test"));
		assertNotNull("Null MAV", mav);
		assertEquals("Wrong view", "success", mav.getViewName());
		assertNotNull("Null user", userDao.findByUserName("test"));
	}
	
	public void testOnSubmitCardSpacePhase1() throws Exception
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_ALL_FIELDS);
		SamlAssertion assertion = TestObjectFactory.getSamlAssertion(doc);
		PublicKey publicKey = TestObjectFactory.getPublicKey(doc);		
		CardSpaceCredentials cred = new CardSpaceCredentials(assertion, publicKey, new Date());
		
		AccountCreateCommand command = new AccountCreateCommand();
		command.setFormComplete(false);
		command.setFormType("INFOCARD");
		command.setXmlToken(cred);
		
		ModelAndView mav = controller.onSubmit(request, response, command, new BindException(new Object(), "test"));
		assertNotNull("Null MAV", mav);
		assertEquals("Wrong view", "form", mav.getViewName());
		assertNotNull("Null spec", command.getCardSpaceTokenSpec());
		assertNotNull("Null email", command.getEmailAddress());
		assertNotNull("Null website", command.getWebsite());
	}

	public void testOnSubmitCardSpacePhase2() throws Exception
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		CardSpaceTokenSpec spec = new CardSpaceTokenSpec("ppid", "issuerHash", new Date(new Date().getTime() + 60000));
		
		AccountCreateCommand command = new AccountCreateCommand();
		command.setFormComplete(true);
		command.setFormType("INFOCARD");
		command.setUserName("test");
		command.setEmailAddress("test@example.com");
		command.setWebsite("http://www.example.com/");
		command.setCardSpaceTokenSpec(spec);
		
		ModelAndView mav = controller.onSubmit(request, response, command, new BindException(new Object(), "test"));
		assertNotNull("Null MAV", mav);
		assertEquals("Wrong view", "success", mav.getViewName());
		User user = userDao.findByUserName("test");
		assertNotNull("Null user", user);
		CardSpaceToken token = cardSpaceTokenDao.findByPrivatePersonalIdentifier("ppid", "issuerHash");
		assertNotNull("Null token", token);
		assertEquals("Wrong user", user, token.getUser());
	}

}
