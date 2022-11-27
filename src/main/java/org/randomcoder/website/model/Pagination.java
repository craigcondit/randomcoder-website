package org.randomcoder.website.model;

public class Pagination {

    public static Pagination of(long page, long size) {
        Pagination p = new Pagination();
        p.page = page;
        p.size = size;
        return p;
    }

    private Long page;
    private Long size;

    public void setPage(Long page) {
        this.page = page;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getPage() {
        return page;
    }

    public Long getSize() {
        return size;
    }

    public long getPageOrDefault(long defaultPage) {
        if (page != null && page.longValue() >= 0L) {
            return page;
        }
        return defaultPage;
    }

    public long getSizeOrDefault(long defaultSize) {
        if (size != null && size.longValue() >= 1L) {
            return size;
        }
        return defaultSize;
    }

}
