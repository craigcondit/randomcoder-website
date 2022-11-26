package org.randomcoder.bo;

import jakarta.inject.Inject;
import org.randomcoder.dao.TagDao;
import org.randomcoder.db.Tag;
import org.randomcoder.io.Consumer;
import org.randomcoder.io.Producer;
import org.randomcoder.tag.TagCloudEntry;
import org.randomcoder.tag.TagNotFoundException;
import org.randomcoder.tag.TagStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Tag management implementation.
 */
@Component("tagBusiness")
public class TagBusinessImpl implements TagBusiness {

    private TagDao tagDao;

    @Inject
    public void setTagDao(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    public List<TagCloudEntry> getTagCloud() {
        var tagStats = tagDao.listAllTagStatistics();
        int mostArticles = tagDao.maxArticleCount();

        List<TagCloudEntry> cloud = new ArrayList<>();
        for (TagStatistics tag : tagStats) {
            if (tag.getArticleCount() > 0) {
                cloud.add(new TagCloudEntry(tag, mostArticles));
            }
        }

        return cloud;
    }

    @Override
    public void loadTagForEditing(Consumer<Tag> consumer, Long tagId) {
        Tag tag = loadTag(tagId);
        consumer.consume(tag);
    }

    @Override
    public void createTag(Producer<Tag> producer) {
        Tag tag = new Tag();
        producer.produce(tag);
        tagDao.save(tag);
    }

    @Override
    public void updateTag(Producer<Tag> producer, Long tagId) {
        Tag tag = loadTag(tagId);
        producer.produce(tag);
        tagDao.save(tag);
    }

    @Override
    public void deleteTag(Long tagId) {
        tagDao.deleteById(tagId);
    }

    @Override
    public Tag findTagByName(String name) {
        return tagDao.findByName(name);
    }

    private Tag loadTag(Long tagId) {
        Tag tag = tagDao.findById(tagId);
        if (tag == null) {
            throw new TagNotFoundException();
        }
        return tag;
    }

    @Override
    public Page<TagStatistics> findTagStatistics(Pageable pageable) {
        var stats = tagDao.listAllTagStatistics(pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(stats.getContent(), pageable, stats.getTotalSize());
    }

}