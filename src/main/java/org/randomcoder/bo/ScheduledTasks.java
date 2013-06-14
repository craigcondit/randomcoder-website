package org.randomcoder.bo;

import javax.inject.*;

import org.apache.commons.logging.*;
import org.randomcoder.download.cache.CachingPackageListProducer;
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
	private CachingPackageListProducer cachingMavenRepository;

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
	 * Sets the caching maven repository.
	 * 
	 * @param cachingMavenRepository
	 *            caching maven repository
	 */
	@Inject
	@Named("cachingMavenRepository")
	public void setCachingMavenRepository(CachingPackageListProducer cachingMavenRepository)
	{
		this.cachingMavenRepository = cachingMavenRepository;
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

	/**
	 * Refreshes the maven repository.
	 */
	@Scheduled(cron = "0 */30 * * * *")
	public void refreshMavenRepository()
	{
		try
		{
			cachingMavenRepository.refresh();
		}
		catch (Exception e)
		{
			logger.error("Error while refreshing maven repository", e);
		}
	}
}
