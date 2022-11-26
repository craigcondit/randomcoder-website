package org.randomcoder.db;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Database entity representing a security role.
 */
public class Role implements Serializable, Comparable<Role> {

    private static final long serialVersionUID = -828314946477973093L;

    /**
     * Role Comparator (by description).
     */
    public static final Comparator<Role> DESCRIPTION_COMPARATOR =
            (r1, r2) -> {
                String s1 = StringUtils.trimToEmpty(r1.getDescription());
                String s2 = StringUtils.trimToEmpty(r2.getDescription());
                return s1.compareTo(s2);
            };

    private Long id;
    private String name;
    private String description;

    /**
     * Gets the id of this role.
     *
     * @return role id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id of this role.
     *
     * @param id role id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of this role.
     *
     * @return role name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this role.
     *
     * @param name role name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of this role.
     *
     * @return role description, or null if not supplied.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this role.
     *
     * @param description role description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Determines if two Role objects are equal.
     *
     * @return true if equal, false if not
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Role role))
            return false;

        // two roles are equal if and only if their names match
        String name1 = StringUtils.trimToEmpty(getName());
        String name2 = StringUtils.trimToEmpty(role.getName());

        return name1.equals(name2);
    }

    /**
     * Gets the hash code of this role.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return StringUtils.trimToEmpty(getName()).hashCode();
    }

    /**
     * Compares this role to another role by description.
     *
     * @return 0 if equal, -1 if this is before, 1 if this is after
     */
    @Override
    public int compareTo(Role o) {
        return DESCRIPTION_COMPARATOR.compare(this, o);
    }

    /**
     * Gets a string representation of this object, suitable for debugging.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return ReflectionToStringBuilder
                .toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
