package com.randomcoder.security.cardspace;

import com.randomcoder.saml.SamlAttributeSpec;

/**
 * Constants representing CardSpace-specific claims.
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
abstract public class CardSpaceAttributes
{
	public static final String CARDSPACE_CLAIMS_SCHEMA = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims";
	
	/**
	 * Private Personal Identifier
	 */
	public static final SamlAttributeSpec PRIVATE_PERSONAL_IDENTIFIER = 
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "privatepersonalidentifier");

	/**
	 * First Name
	 */
	public static final SamlAttributeSpec FIRST_NAME = 
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "givenname");
	
	/**
	 * Last Name
	 */
	public static final SamlAttributeSpec LAST_NAME = 
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "surname");

	/**
	 * Email Address
	 */
	public static final SamlAttributeSpec EMAIL_ADDRESS = 
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "emailaddress");

	/**
	 * Street Address
	 */
	public static final SamlAttributeSpec STREET = 
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "streetaddress");
	
	/**
	 * City or Locality
	 */
	public static final SamlAttributeSpec CITY = 
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "locality");
	
	/**
	 * State or Province
	 */
	public static final SamlAttributeSpec STATE = 
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "stateorprovince");
	
	/**
	 * Postal Code
	 */
	public static final SamlAttributeSpec ZIP = 
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "postalcode");
	
	/**
	 * Country
	 */
	public static final SamlAttributeSpec COUNTRY = 
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "country");
	
	/**
	 * Home Phone
	 */
	public static final SamlAttributeSpec HOME_PHONE = 
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "homephone");
	
	/**
	 * Other Phone
	 */
	public static final SamlAttributeSpec OTHER_PHONE = 
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "otherphone");
	
	/**
	 * Mobile Phone
	 */
	public static final SamlAttributeSpec MOBILE_PHONE = 
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "mobilephone");
	
	/**
	 * Date of Birth
	 */
	public static final SamlAttributeSpec DATE_OF_BIRTH = 
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "dateofbirth");
	
	/**
	 * Gender
	 */
	public static final SamlAttributeSpec GENDER = 
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "gender");
	
	/**
	 * Web page
	 */
	public static final SamlAttributeSpec WEB_PAGE =
		new SamlAttributeSpec(CARDSPACE_CLAIMS_SCHEMA, "webpage");
}
