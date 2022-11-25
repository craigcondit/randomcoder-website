package org.randomcoder.dao;

import java.util.List;

public class Page<T> {
    private final List<T> content;
    private final long offset;
    private final long totalSize;
    private final long pageSize;

    public Page(List<T> content, long offset, long totalSize, long pageSize) {
        this.content = content;
        this.offset = offset;
        this.totalSize = totalSize;
        this.pageSize = pageSize;

        if (offset < 0) {
            throw new IllegalArgumentException("offset must be >= 0");
        }
        if (totalSize < 0) {
            throw new IllegalArgumentException("totalSize must be >= 0");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be >= 1");
        }
    }

    public List<T> content() {
        return content;
    }

    public boolean isFirst() {
        return offset == 0;
    }

    public boolean isLast() {
        return (content.size() + offset) >= totalSize;
    }

    public long getNumber() {
        long page = offset / pageSize;
        if (offset % pageSize != 0) {
            page++;
        }
        return page;
    }

    public long getTotalPages() {
        long pages = totalSize / pageSize;
        if (totalSize % pageSize != 0) {
            pages++;
        }
        return pages;
    }

}
