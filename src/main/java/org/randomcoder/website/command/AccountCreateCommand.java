package org.randomcoder.website.command;

import jakarta.ws.rs.FormParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.randomcoder.website.data.Role;
import org.randomcoder.website.data.User;

import java.util.ArrayList;
import java.util.function.Consumer;

public class AccountCreateCommand implements Consumer<User> {

    private String userName;
    private String emailAddress;
    private String website;
    private String password;
    private String password2;

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

    @Override
    public void accept(User user) {
        user.setUserName(userName);
        user.setEmailAddress(emailAddress);
        user.setWebsite(website);
        user.setEnabled(true);
        user.setPassword(User.hashPassword(password));
        user.setRoles(new ArrayList<Role>());
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }

}
