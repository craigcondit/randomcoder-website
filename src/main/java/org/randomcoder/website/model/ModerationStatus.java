package org.randomcoder.website.model;

public enum ModerationStatus {
    PENDING,
    SPAM,
    HAM;

    public String getName() {
        return name();
    }

    public int getOrdinal() {
        return ordinal();
    }
}
