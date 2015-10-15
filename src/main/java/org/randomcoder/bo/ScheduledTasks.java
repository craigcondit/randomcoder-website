package org.randomcoder.bo;

import javax.inject.*;

import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Various scheduled tasks.
 */
@Component("scheduledTasks")
public class ScheduledTasks
{
	private static final Log logger = LogFactory.getLog(ScheduledTasks.class);

	/**
	 * Default moderation batch size.
	 */
	public static final int DEFAULT_MODERATION_BATCH_SIZE = 5;

	private ArticleBusiness articleBusiness;

	private int moderationBatchSize = DEFAULT_MODERATION_BATCH_SIZE;

	/**
	 * Sets the ArticleBusiness implementation to use.
	 * 
	 * @param articleBusiness
	 *            ArticleBusiness implementation
	 */
	@Inject
	public void setArticleBusiness(ArticleBusiness articleBusiness)
	{
		this.articleBusiness = articleBusiness;
	}

	/**
	 * Sets the number of comments to moderate in each batch.
	 * 
	 * @param moderationBatchSize
	 *            number of comments to moderate
	 */
	@Value("${moderation.batch.size}")
	public void setModerationBatchSize(int moderationBatchSize)
	{
		this.moderationBatchSize = moderationBatchSize;
	}

	/**
	 * Moderates comments.
	 */
	@Scheduled(cron = "*/60 * * * * *")
	public void moderateComments()
	{
		try
		{
			boolean processed = false;
			do
			{
				processed = articleBusiness.moderateComments(moderationBatchSize);
			}
			while (processed);
		}
		catch (Exception e)
		{
			logger.error("Error while moderating comments", e);
		}
	}

}
