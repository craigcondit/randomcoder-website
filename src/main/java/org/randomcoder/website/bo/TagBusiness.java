package org.randomcoder.website.bo;

import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.Tag;
import org.randomcoder.website.model.TagCloudEntry;
import org.randomcoder.website.data.TagStatistics;

import java.util.List;
import java.util.function.Consumer;

public interface TagBusiness {

    List<TagCloudEntry> getTagCloud();

    void createTag(Consumer<Tag> visitor);

    void loadTagForEditing(Consumer<Tag> consumer, Long tagId);

    void updateTag(Consumer<Tag> visitor, Long tagId);

    void deleteTag(Long tagId);

    Tag findTagByName(String name);

    Page<TagStatistics> findTagStatistics(long offset, long length);

}
