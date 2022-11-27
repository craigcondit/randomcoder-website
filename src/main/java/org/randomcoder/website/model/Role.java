package org.randomcoder.website.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Comparator;

public class Role implements Comparable<Role> {

    public static final Comparator<Role> DESCRIPTION_COMPARATOR =
            (r1, r2) -> {
                String s1 = StringUtils.trimToEmpty(r1.getDescription());
                String s2 = StringUtils.trimToEmpty(r2.getDescription());
                return s1.compareTo(s2);
            };

    private Long id;
    private String name;
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Role role)) {
            return false;
        }

        // two roles are equal if and only if their names match
        String name1 = StringUtils.trimToEmpty(getName());
        String name2 = StringUtils.trimToEmpty(role.getName());

        return name1.equals(name2);
    }

    @Override
    public int hashCode() {
        return StringUtils.trimToEmpty(getName()).hashCode();
    }

    @Override
    public int compareTo(Role o) {
        return DESCRIPTION_COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
