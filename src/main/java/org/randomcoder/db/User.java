package org.randomcoder.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * JPA entity representing a user.
 */
@Entity
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SequenceGenerator(name = "users", sequenceName = "users_seq", allocationSize = 1)
public class User implements Serializable {
    private static final long serialVersionUID = 2227663675676869070L;

    private Long id;
    private String userName;
    private String password;
    private String emailAddress;
    private String website;
    private boolean enabled;
    private Date lastLoginDate;

    private List<Role> roles;

    /**
     * Hashes a password.
     *
     * @param password password to hash
     * @return hashed password
     */
    public static String hashPassword(String password) {
        return DigestUtils.sha1Hex(password).toLowerCase(Locale.US);
    }

    /**
     * Gets the id of this user.
     *
     * @return user id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "users")
    @Column(name = "user_id")
    public Long getId() {
        return id;
    }

    /**
     * Sets the id of this user.
     *
     * @param id user id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the roles which this user belongs to.
     *
     * @return Set of roles
     */
    @OneToMany
    @JoinTable(name = "user_role_link", joinColumns = {
            @JoinColumn(name = "user_id")}, inverseJoinColumns = @JoinColumn(name = "role_id"))
    @OrderBy("description")
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * Sets the roles which this user belongs to.
     *
     * @param roles Set of roles
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /**
     * Gets the user name of this user.
     *
     * @return user name
     */
    @Column(name = "username", unique = true, nullable = false, length = 30)
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user name of this user.
     *
     * @param userName user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the password hash of this user.
     *
     * @return password hash
     */
    @Column(name = "password", nullable = true, length = 255)
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password hash of this user.
     *
     * @param password password hash
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the email address of this user.
     *
     * @return email address
     */
    @Column(name = "email", nullable = false, length = 320)
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the email address of this user.
     *
     * @param emailAddress email address
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Gets the web site of this user.
     *
     * @return web site
     */
    @Column(name = "website", nullable = true, length = 255)
    public String getWebsite() {
        return website;
    }

    /**
     * Sets the web site of this user.
     *
     * @param website web site
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * Determines if this user is enabled.
     *
     * @return true if enabled, false otherwise
     */
    @Column(name = "enabled", nullable = false)
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether a user is enabled.
     *
     * @param enabled true if enabled, false otherwise
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the last time this user logged in.
     *
     * @return last login date
     */
    @Column(name = "login_date", unique = false, nullable = true)
    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * Sets the last date this user logged in.
     *
     * @param lastLoginDate last login date
     */
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * Gets a string representation of this object, suitable for debugging.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return (new ReflectionToStringBuilder(this,
                ToStringStyle.SHORT_PREFIX_STYLE) {
            @Override
            protected boolean accept(Field f) {
                if (f.getName().equals("password")) {
                    return false;
                }
                if (f.getName().equals("emailAddress")) {
                    return false;
                }
                if (f.getName().equals("website")) {
                    return false;
                }
                return super.accept(f);
            }
        }).toString();
    }
}
