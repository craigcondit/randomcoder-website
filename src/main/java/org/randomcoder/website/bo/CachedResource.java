package org.randomcoder.website.bo;

import jakarta.ws.rs.core.EntityTag;

import java.util.Date;

public record CachedResource(String path, EntityTag tag, Date lastModified, byte[] content) {
}
