package org.randomcoder.website.model;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class User {

    private Long id;
    private String userName;
    private String password;
    private String emailAddress;
    private String website;
    private boolean enabled;
    private Date lastLoginDate;

    private List<Role> roles = new ArrayList<>();

    public static String hashPassword(String password) {
        return DigestUtils.sha1Hex(password).toLowerCase(Locale.US);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

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
