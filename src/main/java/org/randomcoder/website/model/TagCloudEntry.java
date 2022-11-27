package org.randomcoder.website.model;

import org.randomcoder.website.data.TagStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagCloudEntry extends TagStatistics {

    private static final Logger logger = LoggerFactory.getLogger(TagCloudEntry.class);

    private int scale;

    public TagCloudEntry() {
        super();
    }

    public TagCloudEntry(TagStatistics stat, int maximumArticleCount) {
        super(stat.getTag(), stat.getArticleCount());

        if (maximumArticleCount <= 0) {
            scale = 0;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Tag: "
                        + getTag().getName()
                        + " -> " + getArticleCount()
                        + " / "
                        + maximumArticleCount
                        + " = " + ((getArticleCount() * 10)
                        / maximumArticleCount));
            }

            setScale((getArticleCount() * 10) / maximumArticleCount);
        }
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        if (scale < 0) {
            scale = 0;
        }
        if (scale > 9) {
            scale = 9;
        }
        this.scale = scale;
    }

}
