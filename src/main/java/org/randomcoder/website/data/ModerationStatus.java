package org.randomcoder.website.data;

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
