package org.randomcoder.website.command;

import jakarta.ws.rs.FormParam;
import org.apache.commons.lang3.StringUtils;
import org.randomcoder.website.data.User;

import java.io.Serializable;
import java.util.function.Consumer;

public class UserProfileCommand implements Serializable, Consumer<User> {
    private static final long serialVersionUID = 8464807327958297647L;

    private String emailAddress;
    private String website;

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

    @Override
    public void accept(User target) {
        target.setWebsite(website);
        target.setEmailAddress(emailAddress);
    }

}
