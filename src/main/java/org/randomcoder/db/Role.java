package org.randomcoder.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;
import java.util.Comparator;

/**
 * JPA entity representing a security role.
 */
@Entity
@Immutable
@Table(name = "roles")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@SequenceGenerator(name = "roles", sequenceName = "roles_seq", allocationSize = 1)
public class Role implements Serializable, Comparable<Role> {
    /**
     * Role Comparator (by name).
     */
    public static final Comparator<Role> NAME_COMPARATOR =
            new Comparator<Role>() {
                @Override
                public int compare(Role r1, Role r2) {
                    String s1 = StringUtils.trimToEmpty(r1.getName());
                    String s2 = StringUtils.trimToEmpty(r2.getName());
                    return s1.compareTo(s2);
                }
            };
    /**
     * Role Comparator (by description).
     */
    public static final Comparator<Role> DESCRIPTION_COMPARATOR =
            new Comparator<Role>() {
                @Override
                public int compare(Role r1, Role r2) {
                    String s1 = StringUtils.trimToEmpty(r1.getDescription());
                    String s2 = StringUtils.trimToEmpty(r2.getDescription());
                    return s1.compareTo(s2);
                }
            };
    private static final long serialVersionUID = -828314946477973093L;
    private Long id;
    private String name;
    private String description;

    /**
     * Gets the id of this role.
     *
     * @return role id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "roles")
    @Column(name = "role_id")
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
    @Column(name = "name", unique = true, nullable = false, length = 30)
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
    @Column(name = "description", nullable = true, length = 255)
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
