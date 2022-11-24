package org.randomcoder.bo;

import org.randomcoder.db.Tag;
import org.randomcoder.io.Consumer;
import org.randomcoder.io.Producer;
import org.randomcoder.tag.TagCloudEntry;
import org.randomcoder.tag.TagStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Tag management interface.
 */
public interface TagBusiness {
    /**
     * Gets a list of TagCloudEntry objects to produce a tag cloud.
     *
     * @return list of TagCloudEntry objects sorted by display name.
     */
    List<TagCloudEntry> getTagCloud();

    /**
     * Create a new tag.
     *
     * @param producer tag producer
     */
    void createTag(Producer<Tag> producer);

    /**
     * Loads a tag for editing.
     *
     * @param consumer consumer
     * @param tagId    id of tag to load
     */
    void loadTagForEditing(Consumer<Tag> consumer, Long tagId);

    /**
     * Update an existing tag.
     *
     * @param producer tag producer
     * @param tagId    tag id
     */
    void updateTag(Producer<Tag> producer, Long tagId);

    /**
     * Deletes the tag with the given id.
     *
     * @param tagId tag id
     */
    void deleteTag(Long tagId);

    /**
     * Finds a given {@code Tag} by name.
     *
     * @param name tag name
     * @return {@code Tag} instance, or null if not found
     */
    Tag findTagByName(String name);

    /**
     * Retrieves a page of Tag statistics.
     *
     * @param pageable page to query
     * @return Page of TagStatistics
     */
    Page<TagStatistics> findTagStatistics(Pageable pageable);
}
