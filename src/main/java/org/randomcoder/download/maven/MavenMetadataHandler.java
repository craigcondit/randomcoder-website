package org.randomcoder.download.maven;

import java.text.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ContentHandler which parses Maven metadata.
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
	 * 
	 * @return group id
	 */
	public String getGroupId()
	{
		return groupId;
	}

	/**
	 * Gets the artifactId of this project.
	 * 
	 * @return artifact id
	 */
	public String getArtifactId()
	{
		return artifactId;
	}

	/**
	 * Gets a list of versions available for this project.
	 * 
	 * @return list of versions
	 */
	public List<String> getVersions()
	{
		return versions;
	}

	/**
	 * Gets the tiem when this project was last updated.
	 * 
	 * @return last update date
	 */
	public Date getLastUpdated()
	{
		return lastUpdated;
	}

	/**
	 * Handles document start events.
	 * 
	 * @throws SAXException
	 *           never
	 */
	@Override
	public void startDocument() throws SAXException
	{
		groupId = null;
		artifactId = null;
		versions = new ArrayList<>();
		lastUpdated = null;
		versionsSeen = false;
	}

	/**
	 * Handles element start events.
	 * 
	 * @throws SAXException
	 *           never
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		buf.setLength(0);
		if ("versions".equals(localName))
		{
			versionsSeen = true;
		}
	}

	/**
	 * Handles character data events.
	 * 
	 * @throws SAXException
	 *           never
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		buf.append(ch, start, length);
	}

	/**
	 * Handles element end events.
	 * 
	 * @throws SAXException
	 *           if parsing of data fails
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
			if (versionsSeen)
			{
				versions.add(buf.toString().trim());
			}
		}
	}
}
