package org.randomcoder.website.command;

import jakarta.ws.rs.FormParam;
import org.apache.commons.lang3.StringUtils;
import org.randomcoder.website.data.Tag;
import org.randomcoder.website.validation.DataValidationUtils;

import java.util.function.Consumer;

public class TagAddCommand implements Consumer<Tag> {

    private String name;
    private String displayName;

    public String getName() {
        return name;
    }

    @FormParam("name")
    public void setName(String name) {
        if (name != null) {
            name = name.replaceAll("\\s+", " ").trim();
            name = DataValidationUtils.canonicalizeTagName(name);
            name = StringUtils.trimToNull(name);
        }

        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    @FormParam("displayName")
    public void setDisplayName(String displayName) {
        this.displayName = StringUtils.trimToNull(displayName);
    }

    @Override
    public void accept(Tag tag) {
        if (tag.getId() == null) {
            tag.setName(getName());
        }

        tag.setDisplayName(getDisplayName());
    }

}
