package org.randomcoder.website.command;

import jakarta.ws.rs.FormParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.ContentType;
import org.randomcoder.website.data.Tag;
import org.randomcoder.website.model.TagList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

public class ArticleAddCommand implements Consumer<Article> {

    protected String title;
    protected ContentType contentType;
    protected String permalink;
    protected TagList tags;
    protected String content;
    protected String summary;

    public void load(Article article) {
        this.title = article.getTitle();
        this.contentType = article.getContentType();
        this.permalink = article.getPermalink();
        this.tags = new TagList(article.getTags());
        this.content = article.getContent();
        this.summary = article.getSummary();
    }

    public String getTitle() {
        return title;
    }

    @FormParam("title")
    public void setTitle(String title) {
        this.title = StringUtils.trimToNull(title);
    }

    public ContentType getContentType() {
        return contentType;
    }

    @FormParam("contentType")
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getPermalink() {
        return permalink;
    }

    @FormParam("permalink")
    public void setPermalink(String permalink) {
        this.permalink = StringUtils.stripToNull(
                StringUtils.defaultString(permalink).toLowerCase(Locale.US));
    }

    public TagList getTags() {
        return tags;
    }

    @FormParam("tags")
    public void setTags(TagList tags) {
        this.tags = tags;
    }

    public String getContent() {
        return content;
    }

    @FormParam("content")
    public void setContent(String content) {
        this.content = StringUtils.trimToNull(content);
    }

    public String getSummary() {
        return summary;
    }

    @FormParam("summary")
    public void setSummary(String summary) {
        this.summary = StringUtils.trimToNull(summary);
    }

    @Override
    public void accept(Article article) {
        article.setTitle(title);
        article.setContentType(contentType);
        article.setPermalink(permalink);
        article.setContent(content);
        article.setSummary(summary);

        if (article.getTags() == null) {
            article.setTags(new ArrayList<>());
        }

        Set<Tag> currentTags = new HashSet<>(article.getTags());
        Set<Tag> selectedTags = new HashSet<>(tags.getTags());

        // get list of deleted tags (current - selected)
        Set<Tag> deletedTags = new HashSet<>(currentTags);
        deletedTags.removeAll(selectedTags);

        // get list of added tags (selected - current)
        Set<Tag> addedTags = new HashSet<>(selectedTags);
        addedTags.removeAll(currentTags);

        // remove deleted tags
        article.getTags().removeAll(deletedTags);

        // add new tags
        article.getTags().addAll(addedTags);
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .toString();
    }

}
