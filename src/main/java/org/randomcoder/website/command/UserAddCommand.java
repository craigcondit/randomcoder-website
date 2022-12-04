package org.randomcoder.website.command;

import jakarta.ws.rs.FormParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.randomcoder.db.Role;
import org.randomcoder.db.User;
import org.randomcoder.io.Producer;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

public class UserAddCommand implements Consumer<User> {

    private String userName;
    private String emailAddress;
    private String website;
    private boolean enabled;
    private String password;
    private String password2;

    private Role[] roles;

    public void load(User user) {
        setUserName(user.getUserName());
        setEmailAddress(user.getEmailAddress());
        setWebsite(user.getWebsite());
        setEnabled(user.isEnabled());

        List<Role> roleList = user.getRoles();
        Role[] roleArray = new Role[roleList.size()];
        roleList.toArray(roleArray);

        setRoles(roleArray);
    }

    public String getUserName() {
        return userName;
    }

    @FormParam("userName")
    public void setUserName(String userName) {
        this.userName = StringUtils.trimToNull(userName);
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    @FormParam("emailAddress")
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = StringUtils.trimToNull(emailAddress);
    }

    public String getWebsite() {
        return website;
    }

    @FormParam("website")
    public void setWebsite(String website) {
        this.website = StringUtils.trimToNull(website);
    }

    public boolean isEnabled() {
        return enabled;
    }

    @FormParam("enabled")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPassword() {
        return password;
    }

    @FormParam("password")
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return password2;
    }

    @FormParam("password2")
    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public Role[] getRoles() {
        return roles;
    }

    @FormParam("roles")
    public void setRoles(Role[] roles) {
        this.roles = roles;
    }

    @Override
    public void accept(User user) {
        if (user.getId() == null) {
            user.setUserName(userName); // only for new users
        }

        user.setEmailAddress(emailAddress);
        user.setWebsite(website);
        user.setEnabled(enabled);

        if (password != null && password.trim().length() > 0) {
            user.setPassword(User.hashPassword(password));
        }

        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<Role>());
        }

        Set<Role> currentRoles = new HashSet<>(user.getRoles());
        Set<Role> selectedRoles = new HashSet<>();
        if (roles != null) {
            selectedRoles.addAll(Arrays.asList(roles));
        }

        // get list of deleted roles (current - selected)
        Set<Role> deletedRoles = new HashSet<>(currentRoles);
        deletedRoles.removeAll(selectedRoles);

        // get list of added roles (selected - current)
        Set<Role> addedRoles = new HashSet<>(selectedRoles);
        addedRoles.removeAll(currentRoles);

        // remove deleted roles
        user.getRoles().removeAll(deletedRoles);

        // add new roles
        user.getRoles().addAll(addedRoles);
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .toString();
    }

}
