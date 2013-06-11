package org.randomcoder.article.moderation;

import java.util.TimerTask;

import org.apache.commons.logging.*;
import org.randomcoder.bo.ArticleBusiness;
import org.springframework.beans.factory.annotation.Required;

/**
 * Timer task which handles periodic comment moderation.
 * 
 * <pre>
 * Copyright (c) 2006-2007, Craig Condit. All rights reserved.
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
public class ModeratorTimerTask extends TimerTask
{
	/**
	 * Default batch size.
	 */
	public static final int DEFAULT_BATCH_SIZE = 5;

	private static final Log logger = LogFactory.getLog(ModeratorTimerTask.class);
	
	private ArticleBusiness articleBusiness;
	private int batchSize = DEFAULT_BATCH_SIZE;
	
	/**
	 * Sets the ArticleBusiness implementation to use.
	 * @param articleBusiness ArticleBusiness implementation
	 */
	@Required
	public void setArticleBusiness(ArticleBusiness articleBusiness)
	{
		this.articleBusiness = articleBusiness;
	}
	
	/**
	 * Sets the number of comments to moderate in each batch.
	 * @param batchSize number of comments to moderate
	 */
	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

	/**
	 * Executes a run of this timer task.
	 */
	@Override
	public void run()
	{
		try
		{
			boolean processed = false;
			do
			{
				processed = articleBusiness.moderateComments(batchSize);
			}
			while (processed);
		}
		catch (Exception e)
		{
			logger.error("Error while moderating comments", e);
		}
	}

}
