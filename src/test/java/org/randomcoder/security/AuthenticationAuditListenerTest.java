package org.randomcoder.security;

import static org.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_ALL_FIELDS;

import java.security.PublicKey;
import java.util.*;

import junit.framework.TestCase;

import org.acegisecurity.event.authentication.AuthenticationSuccessEvent;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.springframework.context.ApplicationEvent;
import org.w3c.dom.Document;

import org.randomcoder.cardspace.CardSpaceUtils;
import org.randomcoder.saml.SamlAssertion;
import org.randomcoder.security.cardspace.*;
import org.randomcoder.test.TestObjectFactory;
import org.randomcoder.test.mock.dao.*;
import org.randomcoder.user.*;

@SuppressWarnings("javadoc")
public class AuthenticationAuditListenerTest extends TestCase
{
	private AuthenticationAuditListener aal;
	private SamlAssertion assertion;
	private PublicKey publicKey;
	private UserDaoMock userDao;
	private UserBusinessImpl userBusiness;
	private CardSpaceTokenDaoMock cardSpaceTokenDao;
	private CardSpaceCredentials credentials;
	
	@Override
	protected void setUp() throws Exception
	{
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_ALL_FIELDS);
		assertion = TestObjectFactory.getSamlAssertion(doc);
		publicKey = TestObjectFactory.getPublicKey(doc);
		credentials = new CardSpaceCredentials(assertion, publicKey, new Date());
		
		userDao = new UserDaoMock();
		cardSpaceTokenDao = new CardSpaceTokenDaoMock();
		userBusiness = new UserBusinessImpl();
		userBusiness.setUserDao(userDao);
		userBusiness.setCardSpaceTokenDao(cardSpaceTokenDao);
		aal = new AuthenticationAuditListener();
		aal.setUserBusiness(userBusiness);
		
		User user = new User();
		user.setUserName("test");
		user.setEnabled(true);
		user.setEmailAddress("test@example.com");
		user.setLastLoginDate(null);
		user.setRoles(new ArrayList<Role>());
		userDao.create(user);
		
		CardSpaceToken token = new CardSpaceToken();
		token.setCreationDate(new Date());
		token.setEmailAddress("test@example.com");
		token.setIssuerHash(CardSpaceUtils.calculateIssuerHash(credentials));
		token.setLastLoginDate(null);
		token.setPrivatePersonalIdentifier(credentials.getPrivatePersonalIdentifier());
		token.setUser(user);
		cardSpaceTokenDao.create(token);
	}

	@Override
	protected void tearDown() throws Exception
	{
		aal = null;
		userBusiness = null;
		userDao = null;
		assertion = null;
		publicKey = null;
	}

	public void testOnApplicationEventPassword()
	{
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("test", "test");		
		AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(auth);
		
		User user = userDao.findByUserName("test");
		assertNull("Last login date specified", user.getLastLoginDate());
		aal.onApplicationEvent(event);
		user = userDao.findByUserName("test");
		assertNotNull("Last login date not specified", user.getLastLoginDate());		
	}
	
	public void testOnApplicationEventCardSpace()
	{
		CardSpaceAuthenticationToken auth = new CardSpaceAuthenticationToken(credentials);
		
		CardSpaceToken token = cardSpaceTokenDao.findByPrivatePersonalIdentifier(credentials.getPrivatePersonalIdentifier(), CardSpaceUtils.calculateIssuerHash(credentials));
		AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(auth);
		
		User user = userDao.findByUserName("test");
		assertNull("Last login date specified on user", user.getLastLoginDate());
		assertNull("Last login date specified on token", token.getLastLoginDate());
		aal.onApplicationEvent(event);
		user = userDao.findByUserName("test");
		token = cardSpaceTokenDao.findByPrivatePersonalIdentifier(credentials.getPrivatePersonalIdentifier(), CardSpaceUtils.calculateIssuerHash(credentials));
		assertNotNull("Last login date not specified on user", user.getLastLoginDate());		
		assertNotNull("Last login date not specified on token", token.getLastLoginDate());				
	}
	
	public void testOnApplicationEventUnknown()
	{
		MockApplicationEvent event = new MockApplicationEvent(this);
		aal.onApplicationEvent(event);
	}

	private class MockApplicationEvent extends ApplicationEvent
	{
		private static final long serialVersionUID = 5531856610555293435L;

		public MockApplicationEvent(Object source)
		{
			super(source);
		}
	}
}
