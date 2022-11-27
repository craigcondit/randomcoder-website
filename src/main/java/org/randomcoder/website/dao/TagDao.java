package org.randomcoder.website.dao;

import org.randomcoder.website.data.Tag;
import org.randomcoder.website.data.TagStatistics;
import org.randomcoder.website.data.Page;

import java.util.List;

public interface TagDao {

    Long save(Tag tag);

    void deleteById(long tagId);

    Tag findById(long tagId);

    Tag findByName(String tagName);

    List<Tag> listAll();

    List<TagStatistics> listAllTagStatistics();

    Page<TagStatistics> listAllTagStatistics(long offset, long length);

    int maxArticleCount();

}
