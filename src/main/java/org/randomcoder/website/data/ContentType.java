package org.randomcoder.website.data;

public enum ContentType {
    TEXT("text/plain", "Plain text"),
    XHTML("application/xhtml+xml", "XHTML");

    private final String mimeType;
    private final String description;

    ContentType(String mimeType, String description) {
        this.mimeType = mimeType;
        this.description = description;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name();
    }

    public int getOrdinal() {
        return ordinal();
    }
}
