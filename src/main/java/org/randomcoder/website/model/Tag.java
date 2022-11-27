package org.randomcoder.website.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Comparator;

public class Tag implements Comparable<Tag> {

    public static final Comparator<Tag> NAME_COMPARATOR = (t1, t2) -> {
        String s1 = StringUtils.trimToEmpty(t1.getName());
        String s2 = StringUtils.trimToEmpty(t2.getName());
        return s1.compareTo(s2);
    };

    public static final Comparator<Tag> DISPLAY_NAME_COMPARATOR = (t1, t2) -> {
        String s1 = StringUtils.trimToEmpty(t1.getDisplayName());
        String s2 = StringUtils.trimToEmpty(t2.getDisplayName());
        return s1.compareTo(s2);
    };

    private Long id;
    private String name;
    private String displayName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tag tag)) {
            return false;
        }

        // two tags are equal if and only if their names match
        String name1 = StringUtils.trimToEmpty(getName());
        String name2 = StringUtils.trimToEmpty(tag.getName());

        return name1.equals(name2);
    }

    @Override
    public int hashCode() {
        return StringUtils.trimToEmpty(getName()).hashCode();
    }

    @Override
    public int compareTo(Tag o) {
        return NAME_COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
