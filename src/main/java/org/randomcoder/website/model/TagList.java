package org.randomcoder.website.model;

import org.randomcoder.website.data.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TagList {

    private List<Tag> tags = new ArrayList<>();

    public TagList() {
    }

    public TagList(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.joining(","));
    }

}
