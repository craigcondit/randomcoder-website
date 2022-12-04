package org.randomcoder.website.command;

import jakarta.ws.rs.FormParam;
import org.randomcoder.website.data.Tag;

public class TagEditCommand extends TagAddCommand {

    private Long id;

    public Long getId() {
        return id;
    }

    @FormParam("id")
    public void setId(Long id) {
        this.id = id;
    }

    public void load(Tag tag) {
        setId(tag.getId());
        setName(tag.getName());
        setDisplayName(tag.getDisplayName());
    }

}
