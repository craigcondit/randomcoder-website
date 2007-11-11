package com.randomcoder.feed;

import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import com.randomcoder.about.ApplicationInformation;
import com.randomcoder.article.Article;
import com.randomcoder.content.*;
import com.randomcoder.tag.Tag;
import com.randomcoder.user.User;
import com.randomcoder.validation.DataValidationUtils;
import com.randomcoder.xml.XmlUtils;

/**
 * Generator for Atom 1.0 feeds.
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
public class AtomFeedGenerator implements FeedGenerator
{
	private static final String ATOM_1_0_NS = "http://www.w3.org/2005/Atom";
	private static final String THREAD_NS = "http://purl.org/syndication/thread/1.0";
	private static final String THREAD_NS_PREFIX = "thr";
	private static final String XHTML_NS = "http://www.w3.org/1999/xhtml";
	private static final String XHTML_NS_PREFIX = "xhtml";
	
	private ApplicationInformation applicationInformation;
	private URL baseUrl;
	private String uriPrefix;
	private ContentFilter contentFilter;
	
	/**
	 * Sets the base URL to use for articles.
	 * 
	 * @param baseUrl
	 *          base url
	 */
	@Required
	public void setBaseUrl(String baseUrl) throws MalformedURLException
	{
		this.baseUrl = new URL(baseUrl);
	}
	
	/**
	 * Sets the URI prefix used for generating unique identifiers.
	 * 
	 * @param uriPrefix
	 *          URI prefix
	 */
	@Required
	public void setUriPrefix(String uriPrefix)
	{
		this.uriPrefix = uriPrefix;
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
	
	/**
	 * Sets the ApplicationInformation instance to use.
	 * 
	 * @param applicationInformation
	 *          ApplicationInformation instance
	 */
	@Required
	public void setApplicationInformation(ApplicationInformation applicationInformation)
	{
		this.applicationInformation = applicationInformation;
	}
	
	public String getContentType()
	{
		return "application/atom+xml";
	}

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
		Element root = doc.createElementNS(ATOM_1_0_NS, "feed");
		root.setAttributeNS(XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + THREAD_NS_PREFIX, THREAD_NS);
		root.setAttributeNS(XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + XHTML_NS_PREFIX, XHTML_NS);
		
		doc.appendChild(root);

		// write feed title
		Element titleEl = doc.createElementNS(ATOM_1_0_NS, "title");
		titleEl.appendChild(doc.createTextNode(info.getTitle()));
		root.appendChild(titleEl);
		
		// write feed subtitle
		String subtitle = info.getSubtitle();
		if (subtitle != null)
		{
			Element subtitleEl = doc.createElementNS(ATOM_1_0_NS, "subtitle");
			subtitleEl.appendChild(doc.createTextNode(subtitle));
			root.appendChild(subtitleEl);
		}
		
		// write feed generator
		Element generatorEl = doc.createElementNS(ATOM_1_0_NS, "generator");
		generatorEl.setAttribute("uri", "http://randomcoder.com/");
		generatorEl.setAttribute("version", applicationInformation.getApplicationVersion());
		generatorEl.appendChild(doc.createTextNode(applicationInformation.getApplicationName()));
		root.appendChild(generatorEl);
		
		// write feed URL
		Element feedEl = doc.createElementNS(ATOM_1_0_NS, "link");
		feedEl.setAttribute("rel", "self");
		feedEl.setAttribute("type", "application/atom+xml");
		feedEl.setAttribute("href", info.getFeedUrl().toExternalForm());
		root.appendChild(feedEl);
		
		// write alternate URL
		URL altUrl = info.getAltUrl();
		if (altUrl != null)
		{
			Element altEl = doc.createElementNS(ATOM_1_0_NS, "link");
			altEl.setAttribute("rel", "alternate");
			altEl.setAttribute("type", "text/html");
			altEl.setAttribute("href", altUrl.toExternalForm());
			root.appendChild(altEl);
		}
		
		// write feed id
		Element idEl = doc.createElementNS(ATOM_1_0_NS, "id");
		idEl.appendChild(doc.createTextNode(uriPrefix + info.getFeedId()));
		root.appendChild(idEl);
		
		// write feed updated date
		Element updatedEl = doc.createElementNS(ATOM_1_0_NS, "updated");
		root.appendChild(updatedEl);		
		
		Date feedUpdated = null;
		
		for (Article article : info.getArticles())
		{
			Element entryEl = doc.createElementNS(ATOM_1_0_NS, "entry");
			
			// write title
			Element articleTitleEl = doc.createElementNS(ATOM_1_0_NS, "title");
			articleTitleEl.appendChild(doc.createTextNode(article.getTitle()));			
			entryEl.appendChild(articleTitleEl);
			
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
			Element articleAltEl = doc.createElementNS(ATOM_1_0_NS, "link");
			articleAltEl.setAttribute("rel", "alternate");
			articleAltEl.setAttribute("type", "text/html");
			articleAltEl.setAttribute("href", articleUrl.toExternalForm());
			entryEl.appendChild(articleAltEl);
			
			URL replyUrl = null;
			try
			{
				replyUrl = new URL(articleUrl, "#comments");
			}
			catch (MalformedURLException e)
			{
				throw new FeedException("Unable to generate reply URL", e);
			}
			
			// write article reply link
			Element repliesEl = doc.createElementNS(ATOM_1_0_NS, "link");
			repliesEl.setAttribute("rel", "replies");
			repliesEl.setAttribute("type", "text/html");
			repliesEl.setAttribute("href", replyUrl.toExternalForm());
			repliesEl.setAttributeNS(THREAD_NS, THREAD_NS_PREFIX + ":count", Integer.toString(article.getComments().size()));
			entryEl.appendChild(repliesEl);
			
			// write id
			Element articleIdEl = doc.createElementNS(ATOM_1_0_NS, "id");
			articleIdEl.appendChild(doc.createTextNode(uriPrefix + "article-" + df.format(article.getId())));
			entryEl.appendChild(articleIdEl);
			
			// write published
			Date published = article.getCreationDate();
			Element articlePublishedEl = doc.createElementNS(ATOM_1_0_NS, "published");
			articlePublishedEl.appendChild(doc.createTextNode(formatDate(published)));
			entryEl.appendChild(articlePublishedEl);
			
			if (feedUpdated == null || feedUpdated.before(published))
				feedUpdated = published;
			
			// write modified
			Date updated = article.getModificationDate();
			if (updated != null)
			{
				Element articleUpdatedEl = doc.createElementNS(ATOM_1_0_NS, "updated");
				articleUpdatedEl.appendChild(doc.createTextNode(formatDate(updated)));
				entryEl.appendChild(articleUpdatedEl);
				
				if (feedUpdated == null || feedUpdated.before(updated))
					feedUpdated = updated;
			}
			
			// write author
			String authorName = null;
			String authorUrl = null;
			User createdBy = article.getCreatedByUser();
			if (createdBy == null)
			{
				authorName = "anonymous";
				authorUrl = null;
			}
			else
			{
				authorName = createdBy.getUserName();
				authorUrl = createdBy.getWebsite();
			}
			
			Element authorEl = doc.createElementNS(ATOM_1_0_NS, "author");
			
			Element authorNameEl = doc.createElementNS(ATOM_1_0_NS, "name");
			authorNameEl.appendChild(doc.createTextNode(authorName));
			authorEl.appendChild(authorNameEl);
						
			if (DataValidationUtils.isValidUrl(authorUrl))
			{
				Element authorUriEl = doc.createElementNS(ATOM_1_0_NS, "uri");
				authorUriEl.appendChild(doc.createTextNode(authorUrl));
				authorEl.appendChild(authorUriEl);
			}
			
			entryEl.appendChild(authorEl);
			
			// write categories
			for (Tag tag : article.getTags())
			{
				Element categoryEl = doc.createElementNS(ATOM_1_0_NS, "category");
				categoryEl.setAttribute("term", tag.getName());
				categoryEl.setAttribute("label", tag.getDisplayName());				
				entryEl.appendChild(categoryEl);
			}

			// write summary -- will need content filter
			Element summaryEl = doc.createElementNS(ATOM_1_0_NS, "summary");
			summaryEl.setAttribute("type", "xhtml");
			summaryEl.setAttributeNS(javax.xml.XMLConstants.XML_NS_URI, "xml:lang", "en-US");
			summaryEl.setAttributeNS(javax.xml.XMLConstants.XML_NS_URI, "xml:base", articleUrl.toExternalForm());
			
			try
			{
				addXHTML(doc, summaryEl, article.getSummary(), article.getContentType());
			}
			catch (Exception e)
			{
				throw new FeedException("Unable to generate summary for article with id " + df.format(article.getId()), e);
			}

			entryEl.appendChild(summaryEl);
			
			// write content
			Element contentEl = doc.createElementNS(ATOM_1_0_NS, "summary");
			contentEl.setAttribute("type", "xhtml");
			contentEl.setAttributeNS(javax.xml.XMLConstants.XML_NS_URI, "xml:lang", "en-US");
			contentEl.setAttributeNS(javax.xml.XMLConstants.XML_NS_URI, "xml:base", articleUrl.toExternalForm());
			
			try
			{
				addXHTML(doc, contentEl, article.getContent(), article.getContentType());
			}
			catch (Exception e)
			{
				throw new FeedException("Unable to generate content for article with id " + df.format(article.getId()), e);
			}

			entryEl.appendChild(contentEl);
			
			root.appendChild(entryEl);
		}
		
		if (feedUpdated == null)
			feedUpdated = new Date();
		
		updatedEl.appendChild(doc.createTextNode(formatDate(feedUpdated)));
		
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
	
	private void addXHTML(Document doc, Element parent, String text, ContentType contentType)
	throws TransformerException, SAXException, IOException
	{
		Element root = doc.createElementNS(XHTML_NS, "div");
		Element tempRoot = doc.createElementNS(XHTML_NS, "div");
		
		ContentUtils.formatText(text, contentType, contentFilter, new DOMResult(tempRoot));

		// copy children to remove extra nesting
		Node child = tempRoot.getFirstChild();
		NodeList nl = child.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
		{
			Node node = nl.item(i);
			child.removeChild(node);
			root.appendChild(node);
		}
		
		parent.appendChild(root);
	}
	
	private String formatDate(Date date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		String text = sdf.format(date);
		return  text.substring(0, text.length() - 2) + ":" + text.substring(text.length() - 2);
	}
}