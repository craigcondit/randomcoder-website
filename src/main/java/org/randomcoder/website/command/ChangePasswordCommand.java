package org.randomcoder.website.command;

import jakarta.ws.rs.FormParam;
import org.randomcoder.website.data.User;

public class ChangePasswordCommand {

    private String oldPassword;
    private String password;
    private String password2;
    private User user;

    public String getOldPassword() {
        return oldPassword;
    }

    @FormParam("oldPassword")
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
