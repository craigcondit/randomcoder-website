package org.randomcoder.website.bo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.glassfish.hk2.api.Immediate;
import org.randomcoder.website.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Immediate
public class ScheduledTasks {

    public static final int DEFAULT_MODERATION_BATCH_SIZE = 5;

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private ScheduledExecutorService executor;

    @Inject
    ArticleBusiness articleBusiness;

    @Inject
    @Named(Config.MODERATION_BATCH_SIZE)
    int moderationBatchSize = DEFAULT_MODERATION_BATCH_SIZE;

    @PostConstruct
    public void start() {
        logger.info("Starting scheduled task executor...");
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(this::moderateComments, 60, 60, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void stop() {
        logger.info("Stopping scheduled task executor...");
        if (executor != null) {
            executor.shutdown();
        }
        executor = null;
    }

    public void moderateComments() {
        try {
            boolean processed;
            do {
                processed = articleBusiness.moderateComments(moderationBatchSize);
            } while (processed);
        } catch (Exception e) {
            logger.error("Error while moderating comments", e);
        }
    }

}
