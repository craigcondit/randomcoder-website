package com.randomcoder.saml;

import static com.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_TEST;

import java.io.StringReader;
import java.util.*;

import junit.framework.TestCase;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.randomcoder.security.cardspace.CardSpaceAttributes;
import com.randomcoder.test.TestObjectFactory;
import com.randomcoder.xml.XmlUtils;

public class SamlAssertionTest extends TestCase
{
	private Document assertionDoc;
	private Element assertionEl;
	
	@Override
	public void setUp() throws Exception
	{		
		assertionDoc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_TEST);
		assertionEl = SamlUtils.findFirstSamlAssertion(assertionDoc);
	}

	@Override
	public void tearDown() throws Exception
	{
		assertionDoc = null;
		assertionEl = null;
	}
	
	public void testSamlAssertion() throws Exception
	{
		SamlAssertion assertion = new SamlAssertion(assertionEl);
		assertEquals(SamlVersion.SAML_1_1, assertion.getVersion());		
		assertEquals("SAML 1.1", assertion.getVersion().getDescription());
		assertEquals("uuid:b89586f8-4aa8-4157-9db6-bb10afd471eb", assertion.getAssertionId());
		assertEquals("http://schemas.xmlsoap.org/ws/2005/05/identity/issuer/self", assertion.getIssuer());
		
		Date issueInstant = assertion.getIssueInstant();
		assertNotNull(issueInstant);
		
		Date notBefore = assertion.getNotBefore();
		assertNotNull(notBefore);
		assertFalse("issueInstant after notBefore", issueInstant.after(notBefore));
		
		Date notOnOrAfter = assertion.getNotOnOrAfter();
		assertNotNull(notOnOrAfter);
		assertTrue("notOnOrAfter before notBefore", notOnOrAfter.after(notBefore));
		
		// get assertions
		List<SamlAttribute> attributes = assertion.getAttributes();
		assertNotNull(attributes);
		assertEquals(4, attributes.size());
		
		SamlAttributeSpec spec = attributes.get(0).getAttributeSpec();
		assertEquals(CardSpaceAttributes.PRIVATE_PERSONAL_IDENTIFIER, spec);
		assertEquals(CardSpaceAttributes.CARDSPACE_CLAIMS_SCHEMA, spec.getNamespace());
		assertEquals("privatepersonalidentifier", spec.getLocal());
		assertEquals("DLb/nzDbfMEHkME4VYeny0teGTjvYtVdrQvmX9W055E=", attributes.get(0).getValue());		
	}
	
	public void testSamlAssertionBadNamespace() throws Exception
	{
		try
		{
			Document doc = XmlUtils.parseXml(new InputSource(new StringReader("<data />")));
			Element root = (Element) doc.getElementsByTagName("*").item(0);
			new SamlAssertion(root);
			fail("SamlException expected");
		}
		catch (SamlException e)
		{
			// pass
		}		
	}

	public void testSamlAssertionMissingAssertionID() throws Exception
	{
		try
		{
			assertionEl.removeAttribute("AssertionID");
			new SamlAssertion(assertionEl);
			fail("SamlException expected");
		}
		catch (SamlException e)
		{
			// pass
		}					
	}

	public void testSamlAssertionMissingIssueInstant() throws Exception
	{
		try
		{
			assertionEl.removeAttribute("IssueInstant");
			new SamlAssertion(assertionEl);
			fail("SamlException expected");
		}
		catch (SamlException e)
		{
			// pass
		}					
	}

	public void testSamlAssertionMissingIssuer() throws Exception
	{
		try
		{
			assertionEl.removeAttribute("Issuer");
			new SamlAssertion(assertionEl);		
			fail("SamlException expected");
		}
		catch (SamlException e)
		{
			// pass
		}		
	}

	public void testSamlAssertionMissingConditions() throws Exception
	{
		try
		{
			NodeList conditions = assertionEl.getElementsByTagNameNS(assertionEl.getNamespaceURI(), "Conditions");
			for (int i = 0; i < conditions.getLength(); i++)
			{
				Node node = conditions.item(i);
				node.getParentNode().removeChild(node);
			}
			new SamlAssertion(assertionEl);
			fail("SamlException expected");
		}
		catch (SamlException e)
		{
			// pass
		}		
	}

	public void testSamlAssertionMissingConditionsNotBefore() throws Exception
	{
		try
		{
			NodeList conditions = assertionEl.getElementsByTagNameNS(assertionEl.getNamespaceURI(), "Conditions");
			for (int i = 0; i < conditions.getLength(); i++)
			{
				Element condition = (Element) conditions.item(i);
				condition.removeAttribute("NotBefore");
			}
			new SamlAssertion(assertionEl);		
			fail("SamlException expected");
		}
		catch (SamlException e)
		{
			// pass
		}		
	}

	public void testSamlAssertionMissingConditionsNotOnOrAfter() throws Exception
	{
		try
		{
			NodeList conditions = assertionEl.getElementsByTagNameNS(assertionEl.getNamespaceURI(), "Conditions");
			for (int i = 0; i < conditions.getLength(); i++)
			{
				Element condition = (Element) conditions.item(i);
				condition.removeAttribute("NotOnOrAfter");
			}
			new SamlAssertion(assertionEl);		
			fail("SamlException expected");
		}
		catch (SamlException e)
		{
			// pass
		}		
	}

	public void testSamlAssertionMissingAttributeNamespace() throws Exception
	{
		try
		{
			NodeList attributes = assertionEl.getElementsByTagNameNS(assertionEl.getNamespaceURI(), "Attribute");
			for (int i = 0; i < attributes.getLength(); i++)
			{
				Element att = (Element) attributes.item(i);
				att.removeAttribute("AttributeNamespace");
			}
			new SamlAssertion(assertionEl);		
			fail("SamlException expected");
		}
		catch (SamlException e)
		{
			// pass
		}		
	}

	public void testSamlAssertionMissingAttributeName() throws Exception
	{
		try
		{
			NodeList attributes = assertionEl.getElementsByTagNameNS(assertionEl.getNamespaceURI(), "Attribute");
			for (int i = 0; i < attributes.getLength(); i++)
			{
				Element att = (Element) attributes.item(i);
				att.removeAttribute("AttributeName");
			}
			new SamlAssertion(assertionEl);		
			fail("SamlException expected");
		}
		catch (SamlException e)
		{
			// pass
		}		
	}

	public void testSamlAssertionMissingAttributeValue() throws Exception
	{
		try
		{
			NodeList attValues = assertionEl.getElementsByTagNameNS(assertionEl.getNamespaceURI(), "AttributeValue");
			for (int i = 0; i < attValues.getLength(); i++)
			{
				Node node = attValues.item(i);
				node.getParentNode().removeChild(node);
			}
			new SamlAssertion(assertionEl);		
			fail("SamlException expected");
		}
		catch (SamlException e)
		{
			// pass
		}		
	}	
}
