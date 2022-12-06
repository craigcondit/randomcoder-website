package org.randomcoder.website.bo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.randomcoder.website.cache.ArticleCache;
import org.randomcoder.website.cache.EmptyKey;
import org.randomcoder.website.cache.TagCache;
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

    @Inject
    TagDao tagDao;

    @Inject
    TagCache tagCache;

    @Inject
    ArticleCache articleCache;

    @Override
    public List<TagCloudEntry> getTagCloud() {
        var tagStats = tagCache.tagStatistics().get(EmptyKey.KEY,
                k -> tagDao.listAllTagStatistics());

        int mostArticles = tagCache.maxArticleCount().get(EmptyKey.KEY,
                k -> tagDao.maxArticleCount());

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
        tagCache.clearAll();
    }

    @Override
    public void updateTag(Consumer<Tag> visitor, Long tagId) {
        Tag tag = loadTag(tagId);
        visitor.accept(tag);
        tagDao.save(tag);
        tagCache.clearAll();
        articleCache.clearAll();
    }

    @Override
    public void deleteTag(Long tagId) {
        tagDao.deleteById(tagId);
        tagCache.clearAll();
        articleCache.clearAll();
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