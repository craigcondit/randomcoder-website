package org.randomcoder.db;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Database entity representing a user.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 2227663675676869070L;

    private Long id;
    private String userName;
    private String password;
    private String emailAddress;
    private String website;
    private boolean enabled;
    private Date lastLoginDate;

    private List<Role> roles = new ArrayList<>();

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
        return (new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) {
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
