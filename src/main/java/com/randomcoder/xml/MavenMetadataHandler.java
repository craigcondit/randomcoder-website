package com.randomcoder.xml;

import java.text.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ContentHandler which parses maven metadata. 
 * 
 * <pre>
 * Copyright (c) 2007, Craig Condit. All rights reserved.
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
public class MavenMetadataHandler extends DefaultHandler
{
	private String groupId;
	private String artifactId;
	private List<String> versions = new ArrayList<String>();
	private Date lastUpdated;
	
	private StringBuilder buf = new StringBuilder();
	private boolean versionsSeen = false;
	
	/**
	 * Gets the groupId of this project.
	 * @return group id
	 */
	public String getGroupId()
	{
		return groupId;
	}
	
	/**
	 * Gets the artifactId of this project.
	 * @return artifact id
	 */
	public String getArtifactId()
	{
		return artifactId;
	}
	
	/**
	 * Gets a list of versions available for this project.
	 * @return list of versions
	 */
	public List<String> getVersions()
	{
		return versions;
	}
	
	/**
	 * Gets the tiem when this project was last updated.
	 * @return last update date
	 */
	public Date getLastUpdated()
	{
		return lastUpdated;
	}
	
	/**
	 * Handles document start events.
	 * @throws SAXException never
	 */
	@Override
	public void startDocument() throws SAXException
	{
		groupId = null;
		artifactId = null;
		versions = new ArrayList<String>();
		lastUpdated = null;
		versionsSeen = false;
	}

	/**
	 * Handles element start events.
	 * @throws SAXException never
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		buf.setLength(0);
		if ("versions".equals(localName)) versionsSeen = true;
	}
	
	/**
	 * Handles character data events.
	 * @throws SAXException never
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		buf.append(ch, start, length);
	}

	/**
	 * Handles element end events.
	 * @throws SAXException if parsing of data fails
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("groupId".equals(localName))
		{
			groupId = buf.toString().trim();
		}
		else if ("artifactId".equals(localName))
		{
			artifactId = buf.toString().trim();
		}
		else if ("lastUpdated".equals(localName))
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));			
			try
			{
				lastUpdated = sdf.parse(buf.toString().trim());
			}
			catch (ParseException e)
			{
				throw new SAXException("Unable to parse lastUpdated date", e);
			}
		}
		else if ("version".equals(localName))
		{
			if (versionsSeen) versions.add(buf.toString().trim());
		}
	}
}
