package com.randomcoder.security.cardspace;

import java.io.Serializable;
import java.security.*;
import java.util.*;

import com.randomcoder.saml.*;

/**
 * CardSpace credentials implementation.
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
public final class CardSpaceCredentials implements Serializable
{
	private static final long serialVersionUID = 7792602902586182911L;
	private final Date issueInstant;
	private final Date notBefore;
	private final Date notOnOrAfter;
	private final String assertionId;
	private final String issuer;
	private final SamlVersion version;
	private final byte[] issuerPublicKey;
	private final Map<SamlAttributeSpec, String> attributes;
	
	/**
	 * Creates a CardSpaceCredentials from a decrypted saml assertion. 
	 * @param assertion SAML assertion
	 * @param publicKey public key used to sign the assertion
	 */
	public CardSpaceCredentials(SamlAssertion assertion, PublicKey publicKey)
	{
		issueInstant = assertion.getIssueInstant();
		notBefore = assertion.getNotBefore();
		notOnOrAfter = assertion.getNotOnOrAfter();
		assertionId = assertion.getAssertionId();
		issuer = assertion.getIssuer();
		version = assertion.getVersion();
		issuerPublicKey = publicKey.getEncoded();
		
		Map<SamlAttributeSpec, String> atts = new HashMap<SamlAttributeSpec, String>();
		
		for (SamlAttribute att : assertion.getAttributes())
		{
			atts.put(att.getAttributeSpec(), att.getValue());
		}
		attributes = Collections.unmodifiableMap(atts);
	}
	
	/** 
	 * Gets the issue instant of the credentials.
	 * @return issue instant
	 */
	public Date getIssueInstant()
	{
		return issueInstant;
	}
	
	/**
	 * Gets the starting validity date of these credentials.
	 * @return start date
	 */
	public Date getNotBefore()
	{
		return notBefore;
	}
	
	/**
	 * Gets the expiration date of these credentials.
	 * @return expiration date
	 */
	public Date getNotOnOrAfter()
	{
		return notOnOrAfter;
	}
	
	/**
	 * Gets the assertion ID of these credentials.
	 * @return assertion id
	 */
	public String getAssertionId()
	{
		return assertionId;
	}
	
	/**
	 * Gets the issuer of these credentials.
	 * @return issuer
	 */
	public String getIssuer()
	{
		return issuer;
	}
	
	/**
	 * Gets the encoded public key of the issuer.
	 * @return byte array containing the encoded public key
	 */
	public byte[] getIssuerPublicKey()
	{
		byte[] result = new byte[issuerPublicKey.length];
		System.arraycopy(issuerPublicKey, 0, result, 0, issuerPublicKey.length);
		return result;
	}
	
	/**
	 * Gets the SAML version used to build these credentials.
	 * @return SAML version
	 */
	public SamlVersion getVersion()
	{
		return version;
	}
	
	/**
	 * Gets the PPID claimed by these credentials.
	 * @return PPID
	 */
	public String getPrivatePersonalIdentifier()
	{
		return attributes.get(CardSpaceAttributes.PRIVATE_PERSONAL_IDENTIFIER);	
	}
	
	/**
	 * Gets the first name claimed by these credentials.
	 * @return first name
	 */
	public String getFirstName()
	{
		return attributes.get(CardSpaceAttributes.FIRST_NAME);			
	}

	/**
	 * Gets the last name claimed by these credentials.
	 * @return last name
	 */
	public String getLastName()
	{
		return attributes.get(CardSpaceAttributes.LAST_NAME);			
	}
	
	/**
	 * Gets the email address claimed by these credentials.
	 * @return email address
	 */
	public String getEmailAddress()
	{
		return attributes.get(CardSpaceAttributes.EMAIL_ADDRESS);
	}
	
	/**
	 * Gets the street address claimed by these credentials.
	 * @return street address
	 */
	public String getStreet()
	{
		return attributes.get(CardSpaceAttributes.STREET);
	}
	
	/**
	 * Gets the city (locality) claimed by these credentials.
	 * @return city
	 */
	public String getCity()
	{
		return attributes.get(CardSpaceAttributes.CITY);
	}
	
	/**
	 * Gets the state or province claimed by these credentials.
	 * @return state
	 */
	public String getState()
	{
		return attributes.get(CardSpaceAttributes.STATE);		
	}
	
	/**
	 * Gets the ZIP code or postal code for these credentials.
	 * @return zip code
	 */
	public String getZip()
	{
		return attributes.get(CardSpaceAttributes.ZIP);
	}
	
	/**
	 * Gets the country claimed by these credentials.
	 * @return country
	 */
	public String getCountry()
	{
		return attributes.get(CardSpaceAttributes.COUNTRY);
	}
	
	/**
	 * Gets the home phone number claimed by these credentials.
	 * @return home phone
	 */
	public String getHomePhone()
	{
		return attributes.get(CardSpaceAttributes.HOME_PHONE);
	}
	
	/**
	 * Gets the "other" phone number claimed by these credentials.
	 * @return other phone
	 */
	public String getOtherPhone()
	{
		return attributes.get(CardSpaceAttributes.OTHER_PHONE);		
	}
	
	/**
	 * Gets the mobile phone number claimed by these credentials.
	 * @return mobile phone
	 */
	public String getMobilePhone()
	{
		return attributes.get(CardSpaceAttributes.MOBILE_PHONE);
	}
	
	/**
	 * Gets the date of birth claimed by these credentials.
	 * @return date of birth
	 */
	public Date getDateOfBirth()
	{
		String value = attributes.get(CardSpaceAttributes.DATE_OF_BIRTH);
		if (value == null) return null;
		Date dob = null;
		try
		{
			dob = SamlUtils.parseXsdDateTime(value);
		}
		catch (SamlException e) {}
		
		return dob;
	}
	
	/**
	 * Gets the gender claimed by these credentials.
	 * @return gender
	 */
	public Gender getGender()
	{
		String value = attributes.get(CardSpaceAttributes.GENDER);
		if ("1".equals(value)) return Gender.MALE;
		if ("2".equals(value)) return Gender.FEMALE;
		return Gender.UNSPECIFIED;
	}
	
	/**
	 * Gets the web page claimed by these credentials.
	 * @return web page
	 */
	public String getWebPage()
	{
		return attributes.get(CardSpaceAttributes.WEB_PAGE);
	}
	
	/**
	 * Gets the claims associated with these credentials.
	 * @return map of saml attribute specs to values.
	 */
	public Map<SamlAttributeSpec, String> getClaims()
	{
		return attributes;
	}	
}
