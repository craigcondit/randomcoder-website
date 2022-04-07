package org.randomcoder.bo;

import org.randomcoder.db.Tag;
import org.randomcoder.db.TagRepository;
import org.randomcoder.io.Consumer;
import org.randomcoder.io.Producer;
import org.randomcoder.tag.TagCloudEntry;
import org.randomcoder.tag.TagNotFoundException;
import org.randomcoder.tag.TagStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Tag management implementation.
 */
@Component("tagBusiness") public class TagBusinessImpl implements TagBusiness {
  private TagRepository tagRepository;

  /**
   * Sets the tag repository to use.
   *
   * @param tagRepository tag repository
   */
  @Inject public void setTagRepository(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Override @Transactional(value = "transactionManager", readOnly = true)
  public List<TagCloudEntry> getTagCloud() {
    List<TagStatistics> tagStats = tagRepository.findAllTagStatistics();
    int mostArticles = tagRepository.maxArticleCount();

    List<TagCloudEntry> cloud = new ArrayList<>(tagStats.size());

    for (TagStatistics tag : tagStats) {
      if (tag.getArticleCount() > 0) {
        cloud.add(new TagCloudEntry(tag, mostArticles));
      }
    }

    return cloud;
  }

  @Override @Transactional(value = "transactionManager", readOnly = true)
  public void loadTagForEditing(Consumer<Tag> consumer, Long tagId) {
    Tag tag = loadTag(tagId);
    consumer.consume(tag);
  }

  @Override @Transactional("transactionManager")
  public void createTag(Producer<Tag> producer) {
    Tag tag = new Tag();
    producer.produce(tag);
    tagRepository.save(tag);
  }

  @Override @Transactional("transactionManager")
  public void updateTag(Producer<Tag> producer, Long tagId) {
    Tag tag = loadTag(tagId);
    producer.produce(tag);
    tagRepository.save(tag);
  }

  @Override @Transactional("transactionManager")
  public void deleteTag(Long tagId) {
    Tag tag = tagRepository.getOne(tagId);
    if (tag == null) {
      return;
    }

    tagRepository.delete(tag);
  }

  @Override @Transactional(value = "transactionManager", readOnly = true)
  public Tag findTagByName(String name) {
    return tagRepository.findByName(name);
  }

  private Tag loadTag(Long tagId) {
    Tag tag = tagRepository.getOne(tagId);
    if (tag == null) {
      throw new TagNotFoundException();
    }
    return tag;
  }

  @Override @Transactional(value = "transactionManager", readOnly = true)
  public Page<TagStatistics> findTagStatistics(Pageable pageable) {
    return tagRepository.findAllTagStatistics(pageable);
  }
}