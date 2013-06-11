package org.randomcoder.feed;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.Date;

import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.randomcoder.article.Article;
import org.randomcoder.bo.AppInfoBusiness;
import org.randomcoder.content.*;
import org.randomcoder.tag.Tag;
import org.randomcoder.xml.XmlUtils;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Generator for RSS 1.0 feeds.
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
public class Rss20FeedGenerator implements FeedGenerator
{
	private AppInfoBusiness appInfoBusiness;
	private URL baseUrl;
	private ContentFilter contentFilter;
	
	/**
	 * Sets the application information for this feed.
	 * 
	 * @param appInfoBusiness
	 *          application information
	 */
	@Required
	public void setAppInfoBusiness(AppInfoBusiness appInfoBusiness)
	{
		this.appInfoBusiness = appInfoBusiness;
	}
	
	/**
	 * Sets the base URL to use for articles.
	 * 
	 * @param baseUrl
	 *            base url
	 * @throws MalformedURLException if URL is invalid
	 */
	@Required
	public void setBaseUrl(String baseUrl) throws MalformedURLException
	{
		this.baseUrl = new URL(baseUrl);
	}

	/**
	 * Sets the content filter to use for transforming articles into XHTML.
	 * 
	 * @param contentFilter
	 *          content filter
	 */
	@Required
	public void setContentFilter(ContentFilter contentFilter)
	{
		this.contentFilter = contentFilter;
	}

	@Override
	public String generateFeed(FeedInfo info) throws FeedException
	{
		// need to write out XML
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		
		DecimalFormat df = new DecimalFormat("####################");
		
		DocumentBuilder db = null;
		try
		{
			db = dbf.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw new FeedConfigurationException("Unable to generate document builder", e);
		}
		
		Document doc = db.newDocument();
		Element root = doc.createElement("rss");
		root.setAttribute("version", "2.0");
		doc.appendChild(root);
		
		Element channelEl = doc.createElement("channel");

		Element titleEl = doc.createElement("title");
		titleEl.appendChild(doc.createTextNode(info.getTitle()));
		channelEl.appendChild(titleEl);
		
		Element linkEl = doc.createElement("link");
		linkEl.appendChild(doc.createTextNode(info.getAltUrl().toExternalForm()));
		channelEl.appendChild(linkEl);
		
		String subtitle = info.getSubtitle();
		if (subtitle != null)
		{
			Element descriptionEl = doc.createElement("description");
			descriptionEl.appendChild(doc.createTextNode(subtitle));
			channelEl.appendChild(descriptionEl);
		}
		
		Element docsEl = doc.createElement("docs");
		docsEl.appendChild(doc.createTextNode("http://blogs.law.harvard.edu/tech/rss"));
		channelEl.appendChild(docsEl);
		
		Element generatorEl = doc.createElement("generator");
		generatorEl.appendChild(doc.createTextNode(appInfoBusiness.getApplicationName() + " " + appInfoBusiness.getApplicationVersion()));
		channelEl.appendChild(generatorEl);

		Element languageEl = doc.createElement("language");
		languageEl.appendChild(doc.createTextNode("en-us"));
		channelEl.appendChild(languageEl);

		Element pubDateEl = doc.createElement("pubDate");		
		channelEl.appendChild(pubDateEl);

		Date feedUpdated = null;
		
		for (Article article : info.getArticles())
		{
			Element itemEl = doc.createElement("item");
			
			// write title
			Element articleTitleEl = doc.createElement("title");
			articleTitleEl.appendChild(doc.createTextNode(article.getTitle()));			
			itemEl.appendChild(articleTitleEl);
			
			URL articleUrl = null;
			try
			{
				articleUrl = new URL(baseUrl, article.getPermalinkUrl());
			}
			catch (MalformedURLException e)
			{
				throw new FeedException("Unable to generate feed URL", e);
			}

			// write article link
			Element articleLinkEl = doc.createElement("link");
			articleLinkEl.appendChild(doc.createTextNode(articleUrl.toExternalForm()));
			itemEl.appendChild(articleLinkEl);
			
			// write guid
			Element articleGuidEl = doc.createElement("guid");
			if (article.getPermalink() != null)
				articleGuidEl.setAttribute("isPermaLink", "true");
			
			articleGuidEl.appendChild(doc.createTextNode(articleUrl.toExternalForm()));
			itemEl.appendChild(articleGuidEl);
			
			// write published
			Date published = article.getCreationDate();
			Element articlePubDateEl = doc.createElement("pubDate");
			articlePubDateEl.appendChild(doc.createTextNode(formatDate(published)));
			itemEl.appendChild(articlePubDateEl);
			
			if (feedUpdated == null || feedUpdated.before(published))
				feedUpdated = published;
			
			// write categories
			for (Tag tag : article.getTags())
			{
				Element categoryEl = doc.createElement("category");
				categoryEl.appendChild(doc.createTextNode(tag.getDisplayName()));
				itemEl.appendChild(categoryEl);
			}

			// write content
			Element descriptionEl = doc.createElement("description");
			
			String content = article.getSummary();
			if (content == null)
				content = article.getContent();
			
			try
			{
				addXHTML(doc, descriptionEl, content, article.getContentType(), articleUrl);
			}
			catch (Exception e)
			{
				throw new FeedException("Unable to generate description for article with id " + df.format(article.getId()), e);
			}
			
			itemEl.appendChild(descriptionEl);
						
			channelEl.appendChild(itemEl);
		}
				
		if (feedUpdated == null)
			feedUpdated = new Date();

		pubDateEl.appendChild(doc.createTextNode(formatDate(feedUpdated)));

		root.appendChild(channelEl);
		
		StringWriter writer = new StringWriter();		
		try
		{
			XmlUtils.prettyPrint(doc, new StreamResult(writer));
		}
		catch (TransformerException e)
		{
			throw new FeedException("Unable to generate XML for feed", e);
		}
		
		return writer.toString();	
	}

	@Override
	public String getContentType()
	{
		return "application/rss+xml";
	}
	
	private String formatDate(Date date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
		String text = sdf.format(date);
		return  text.substring(0, text.length() - 2) + ":" + text.substring(text.length() - 2);
	}
	
	private void addXHTML(Document doc, Element parent, String text, ContentType contentType, URL articleUrl)
	throws TransformerException, SAXException, IOException
	{
		String content = ContentUtils.formatText(text, articleUrl, contentType, contentFilter);
		parent.appendChild(doc.createTextNode(content));
	}
}
