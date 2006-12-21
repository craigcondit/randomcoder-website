package com.randomcoder.saml;

import java.util.*;

import org.w3c.dom.*;

/**
 * SAML processing methods.
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
public final class SamlUtils
{
	private SamlUtils() {}
	
	/**
	 * SAML 1.0 Namespace
	 */
	public static final String SAML_10_NS = "urn:oasis:names:tc:SAML:1.0:assertion";
	
	private static final String SAML_EL_ASSERTION = "Assertion";
	
	/**
	 * Finds the first SAML <code>Assertion</code> element in the given document.
	 * @param doc DOM document
	 * @return Element, or null if not found
	 */
	public static Element findFirstSamlAssertion(Document doc)
	{
		NodeList list = doc.getElementsByTagNameNS(SAML_10_NS, SAML_EL_ASSERTION);
		if (list == null || list.getLength() == 0) return null;		
		return (Element) list.item(0);
	}
	
	/**
	 * Parses an XML Schema datetime value into a Date object.
	 * @param value XSD datetime value
	 * @return Date
	 */
	public static Date parseXsdDateTime(String value)
	throws SamlException
	{
		try
		{
			String[] dateTime = value.split("T");
			String date = dateTime[0];
			String time = dateTime[1];
			String[] ymd = date.split("-");
			int year = Integer.parseInt(ymd[0]);
			int month = Integer.parseInt(ymd[1]) - 1;
			int day = Integer.parseInt(ymd[2]);
			String[] hms = time.split(":");
			int hour = Integer.parseInt(hms[0]);
			int minutes = Integer.parseInt(hms[1]);
			int seconds = Integer.parseInt(hms[2].substring(0, 2));
			TimeZone tz = TimeZone.getTimeZone("GMT+00:00");
			Calendar cal = Calendar.getInstance(tz, Locale.US);
			cal.set(year, month, day, hour, minutes, seconds);
			return cal.getTime();
		}
		catch (Exception e)
		{
			throw new SamlException(e);
		}
	}
	
	
}
