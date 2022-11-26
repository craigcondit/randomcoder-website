package org.randomcoder.dao;

import org.randomcoder.db.Tag;
import org.randomcoder.tag.TagStatistics;

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
