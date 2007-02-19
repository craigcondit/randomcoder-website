package com.randomcoder.article.moderation;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Required;

import com.randomcoder.about.ApplicationInformation;
import com.randomcoder.article.comment.*;
import com.randomcoder.user.User;

/**
 * Moderator implementation which queries Akismet.
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
public class AkismetModerator implements Moderator, InitializingBean, DisposableBean
{
	private static final Log logger = LogFactory.getLog(AkismetModerator.class);
	
	private MultiThreadedHttpConnectionManager connectionManager;
	
	private String apiKey;
	private String siteUrl;
	private String userAgent;
	
	/**
	 * Sets the Akismet API key to use.
	 * @param apiKey API key
	 */
	@Required
	public void setApiKey(String apiKey)
	{
		this.apiKey = apiKey;
	}
	
	/**
	 * Sets the base url of the client site.
	 * @param siteUrl site url
	 */
	@Required
	public void setSiteUrl(String siteUrl)
	{
		this.siteUrl = siteUrl;
	}
	
	/**
	 * Sets the application information for this client.
	 * @param applicationInformation application information
	 */
	@Required
	public void setApplicationInformation(ApplicationInformation applicationInformation)
	{
		String appName = applicationInformation.getApplicationName();
		String appVersion = applicationInformation.getApplicationVersion();
		
		appName = appName.replaceAll("\\s+", "-");
		appVersion = appVersion.replaceAll("\\s+", "_");
		
		userAgent = appName + "/" + appVersion + " | " + getClass().getCanonicalName() + "/" + appVersion; 
		
		logger.info("Using Akismet user agent: " + userAgent);
	}
	
	/**
	 * Initializes the Akismet filter. 
	 * @throws Exception if an error occurs
	 */
	public void afterPropertiesSet() throws Exception
	{
		connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setStaleCheckingEnabled(true);
		
		logger.info("Logging into Akismet...");
		if (isAkismetAccountValid())
		{
			logger.info("Akismet login successful");
		}
		else
		{
			logger.warn("Akismet login failed... Check api key and site url.");
		}		
	}
		
	/**
	 * Gets the Akismet URL to use for account verification.
	 * @return verification url
	 */
	protected String getVerifyKeyUrl()
	{
		return "http://rest.akismet.com/1.1/verify-key";
	}

	/**
	 * Gets the Akismet URL to use for comment checking.
	 * @return comment check url
	 */
	protected String getCommentCheckUrl()
	{
		return "http://" + apiKey + ".rest.akismet.com/1.1/comment-check";
	}

	/**
	 * Gets the Akismet URL to use for spam submission.
	 * @return submit spam url
	 */
	protected String getSubmitSpamUrl()
	{
		return "http://" + apiKey + ".rest.akismet.com/1.1/submit-spam";
	}

	/**
	 * Gets the Akismet URL to use for ham submission.
	 * @return submit ham url
	 */
	protected String getSubmitHamUrl()
	{
		return "http://" + apiKey + ".rest.akismet.com/1.1/submit-ham";
	}
	
	/**
	 * Shuts down the Akismet filter.
	 * @throws Exception if an error occurs
	 */
	public void destroy() throws Exception
	{
		connectionManager.shutdown();
	}

	public boolean validate(Comment comment) throws ModerationException
	{
		PostMethod post = null;
		try
		{
			HttpClient client = new HttpClient(connectionManager);
			post = new PostMethod(getCommentCheckUrl());
			populatePost(post, comment);
			int status = client.executeMethod(post); 
			if (status != HttpStatus.SC_OK)
			{
				throw new ModerationException("Unknown status code from validation service:" + status);
			}
			
			String body = post.getResponseBodyAsString().toLowerCase(Locale.US);
			
			return "false".equals(body);			
		}
		catch (IOException e)
		{
			throw new ModerationException("Unable to communicate with validation service", e);
		}
		finally
		{
			if (post != null) try { post.releaseConnection(); } catch (Exception ignored) {}						
		}
	}
		
	public void markAsHam(Comment comment) throws ModerationException
	{
		PostMethod post = null;
		try
		{
			HttpClient client = new HttpClient(connectionManager);
			post = new PostMethod(getSubmitHamUrl());
			populatePost(post, comment);
			int status = client.executeMethod(post); 
			if (status != HttpStatus.SC_OK)
			{
				throw new ModerationException("Unknown status code from validation service:" + status);
			}
		}
		catch (IOException e)
		{
			throw new ModerationException("Unable to communicate with validation service", e);
		}
		finally
		{
			if (post != null) try { post.releaseConnection(); } catch (Exception ignored) {}						
		}
	}

	public void markAsSpam(Comment comment) throws ModerationException
	{
		PostMethod post = null;
		try
		{
			HttpClient client = new HttpClient(connectionManager);
			post = new PostMethod(getSubmitSpamUrl());
			populatePost(post, comment);
			int status = client.executeMethod(post); 
			if (status != HttpStatus.SC_OK)
			{
				throw new ModerationException("Unknown status code from validation service:" + status);
			}
		}
		catch (IOException e)
		{
			throw new ModerationException("Unable to communicate with validation service", e);
		}
		finally
		{
			if (post != null) try { post.releaseConnection(); } catch (Exception ignored) {}						
		}
	}

	private void populatePost(PostMethod post, Comment comment)
	{
		CommentIp ip = comment.getIpAddress();			
		CommentUserAgent ua = comment.getUserAgent();
		CommentReferrer ref = comment.getReferrer();
		User user = comment.getCreatedByUser();
		String author = (user == null) ? comment.getAnonymousUserName() : user.getUserName();
		String email = (user == null) ? comment.getAnonymousEmailAddress() : user.getEmailAddress();
		String url = (user == null) ? comment.getAnonymousWebsite() : user.getWebsite();
		
		post.setRequestHeader("User-Agent", userAgent);
		post.addParameter("blog", siteUrl);
		post.addParameter("user_ip", (ip == null) ? "" : ip.getIpAddress());
		post.addParameter("user_agent", (ua == null) ? "" : ua.getUserAgentName());
		post.addParameter("referrer", (ref == null) ? "" : ref.getReferrerUri());
		post.addParameter("permalink", siteUrl + comment.getArticle().getPermalinkUrl());
		post.addParameter("comment_type", "comment");
		if (author != null)
			post.addParameter("comment_author", author);
		if (email != null)
			post.addParameter("comment_author_email", email);
		if (url != null)
			post.addParameter("comment_author_url", url);
		post.addParameter("comment_content", comment.getContent());
	}
	
	private boolean isAkismetAccountValid()
	{
		PostMethod post = null;
		try
		{
			HttpClient client = new HttpClient(connectionManager);
			
			post = new PostMethod(getVerifyKeyUrl());
			post.setRequestHeader("User-Agent", userAgent);
			post.addParameter("key", apiKey);
			post.addParameter("blog", siteUrl);
			
			if (client.executeMethod(post) != HttpStatus.SC_OK)
				return false;
			
			String body = post.getResponseBodyAsString().toLowerCase(Locale.US);
			
			return "valid".equals(body);			
		}
		catch (Exception e)
		{
			logger.error("Caught exception", e);
			return false;
		}
		finally
		{
			if (post != null) try { post.releaseConnection(); } catch (Exception ignored) {}			
		}
	}
}