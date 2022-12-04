package org.randomcoder.website.command;

import jakarta.ws.rs.FormParam;
import org.randomcoder.db.Role;
import org.randomcoder.db.User;
import org.randomcoder.io.Consumer;

import java.util.List;

public class UserEditCommand extends UserAddCommand {

    private Long id;

    public Long getId() {
        return id;
    }

    @FormParam("id")
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void load(User user) {
        setId(user.getId());

        super.load(user);
    }

}
