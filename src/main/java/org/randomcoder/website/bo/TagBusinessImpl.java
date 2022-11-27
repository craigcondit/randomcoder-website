package org.randomcoder.website.bo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.randomcoder.website.dao.TagDao;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.Tag;
import org.randomcoder.website.data.TagNotFoundException;
import org.randomcoder.website.data.TagStatistics;
import org.randomcoder.website.model.TagCloudEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Singleton
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
        consumer.accept(tag);
    }

    @Override
    public void createTag(Consumer<Tag> visitor) {
        Tag tag = new Tag();
        visitor.accept(tag);
        tagDao.save(tag);
    }

    @Override
    public void updateTag(Consumer<Tag> visitor, Long tagId) {
        Tag tag = loadTag(tagId);
        visitor.accept(tag);
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
    public Page<TagStatistics> findTagStatistics(long offset, long length) {
        return tagDao.listAllTagStatistics(offset, length);
    }

}