package org.randomcoder.user;

import static org.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_ALL_FIELDS;

import java.security.PublicKey;
import java.util.Date;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import org.randomcoder.crypto.CertificateContext;
import org.randomcoder.saml.SamlAssertion;
import org.randomcoder.security.cardspace.CardSpaceCredentials;
import org.randomcoder.test.TestObjectFactory;

@SuppressWarnings("javadoc")
public class CardSpaceCredentialsPropertyEditorTest extends TestCase
{
	private CardSpaceCredentialsPropertyEditor editor;
	private CertificateContext certificateContext;
	
	@Override
	protected void setUp() throws Exception
	{
		certificateContext = TestObjectFactory.getCertificateContext();		
		editor = new CardSpaceCredentialsPropertyEditor(certificateContext);		
	}

	@Override
	protected void tearDown() throws Exception
	{
		editor = null;
		certificateContext = null;
	}

	public void testGetAsText() throws Exception
	{
		// always empty string
		assertEquals("", editor.getAsText());
	}

	public void testSetAsText() throws Exception
	{		
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_ALL_FIELDS);
		SamlAssertion assertion = TestObjectFactory.getSamlAssertion(doc);
		PublicKey publicKey = TestObjectFactory.getPublicKey(doc);		
		CardSpaceCredentials cred = new CardSpaceCredentials(assertion, publicKey, new Date());
		
		String xmlToken = TestObjectFactory.getResourceAsString(RESOURCE_SAML_ASSERTION_ALL_FIELDS);
		editor.setAsText(xmlToken);
		
		Object value = editor.getValue();
		assertNotNull("Null value", value);
		assertTrue("Wrong class", value instanceof CardSpaceCredentials);
		
		CardSpaceCredentials parsed = (CardSpaceCredentials) value;
		
		assertEquals("Wrong ppid", cred.getPrivatePersonalIdentifier(), parsed.getPrivatePersonalIdentifier());
	}
	
	public void testSetAsTextEmpty() throws Exception
	{
		editor.setAsText("");
		assertNull(editor.getValue());
	}

}
