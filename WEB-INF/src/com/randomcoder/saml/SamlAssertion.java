package com.randomcoder.saml;

import java.io.Serializable;
import java.util.*;

import org.w3c.dom.*;

/**
 * Object representing a SAML Assertion.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public class SamlAssertion implements Serializable
{
	private static final long serialVersionUID = -4607090848871612610L;
	
	private SamlVersion version;
	private String assertionId;
	private Date issueInstant;
	private Date notBefore;
	private Date notOnOrAfter;
	private String issuer;
	private List<SamlAttribute> attributes;

	/**
	 * Creates a new SAML assertion from the given element.
	 * @param assertion assertion element
	 * @throws SamlException if parsing fails
	 */
	public SamlAssertion(Element assertion) throws SamlException
	{
		if (!SamlUtils.SAML_10_NS.equals(assertion.getNamespaceURI()))
			throw new SamlException("Unknown assertion namespace");

		version = SamlVersion.SAML_1_0;
		if ("1".equals(assertion.getAttribute("MajorVersion")) && "1".equals(assertion.getAttribute("MinorVersion")))
			version = SamlVersion.SAML_1_1;

		assertionId = assertion.getAttribute("AssertionID");
		if (assertionId == null)
			throw new SamlException("Missing Assertion.AssertionID");

		String issueInstantValue = assertion.getAttribute("IssueInstant");
		if (issueInstantValue == null)
			throw new SamlException("Missing Assertion.IssueInstant");
		issueInstant = SamlUtils.parseXsdDateTime(issueInstantValue);

		issuer = assertion.getAttribute("Issuer");
		if (issuer == null)
			throw new SamlException("Missing Assertion.Issuer");

		// get conditions
		NodeList conditions = assertion.getElementsByTagNameNS(assertion.getNamespaceURI(), "Conditions");
		if (conditions == null || conditions.getLength() == 0)
			throw new SamlException("Missing Conditions");

		Element conditionsElement = (Element) conditions.item(0);

		String notBeforeValue = conditionsElement.getAttribute("NotBefore");
		if (notBeforeValue == null)
			throw new SamlException("Missing Conditions.NotBefore");
		notBefore = SamlUtils.parseXsdDateTime(notBeforeValue);

		String notOnOrAfterValue = conditionsElement.getAttribute("NotOnOrAfter");
		if (notOnOrAfterValue == null)
			throw new SamlException("Missing Conditions.NotOnOrAfter");
		notOnOrAfter = SamlUtils.parseXsdDateTime(notOnOrAfterValue);

		// get attributes
		NodeList atts = assertion.getElementsByTagNameNS(assertion.getNamespaceURI(), "Attribute");
		if (atts == null)
			throw new SamlException("Missing Attributes");

		attributes = new ArrayList<SamlAttribute>();
		for (int i = 0; i < atts.getLength(); i++)
		{
			Element att = (Element) atts.item(i);
			attributes.add(new SamlAttribute(att));
		}
	}

	/**
	 * Gets the version of this assertion.
	 * @return SAML version
	 */
	public SamlVersion getVersion()
	{
		return version;
	}

	/**
	 * Gets the assertion ID associated with this assertion.
	 * @return assertion ID
	 */
	public String getAssertionId()
	{
		return assertionId;
	}

	/**
	 * Gets the instant this assertion was issued.
	 * @return issue instant
	 */
	public Date getIssueInstant()
	{
		return issueInstant;
	}

	/**
	 * Gets the issuer of this assertion.
	 * @return issuer
	 */
	public String getIssuer()
	{
		return issuer;
	}

	/**
	 * Gets the earliest date this assertion is valid. 
	 * @return assertion start date
	 */
	public Date getNotBefore()
	{
		return notBefore;
	}

	/**
	 * Gets the expiration date of this assertion.
	 * @return assertion expiration date
	 */
	public Date getNotOnOrAfter()
	{
		return notOnOrAfter;
	}

	/**
	 * Gets a list of the SAML attributes associated with this assertion.
	 * @return list of attributes
	 */
	public List<SamlAttribute> getAttributes()
	{
		return attributes;
	}
}
