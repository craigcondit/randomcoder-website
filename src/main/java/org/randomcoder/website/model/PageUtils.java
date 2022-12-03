package org.randomcoder.website.model;

import jakarta.ws.rs.core.UriInfo;

public final class PageUtils {

    public static final String PARAM_PAGE_NUMBER = "page.page";
    public static final String PARAM_PAGE_SIZE = "page.size";

    private PageUtils() {}

    public static OffsetAndLength parsePagination(UriInfo uriInfo, long defaultPageSize, long maximumPageSize) {
        // set range and sort order
        long length = getLongQueryParam(uriInfo, PARAM_PAGE_SIZE, defaultPageSize);
        long page = getLongQueryParam(uriInfo, PARAM_PAGE_NUMBER, 0);
        if (length > maximumPageSize) {
            length = maximumPageSize;
            page = 0;
        }
        if (length < 1) {
            length = 1;
            page = 0;
        }
        long offset = page * length;
        return new OffsetAndLength(offset, length);
    }

    private static long getLongQueryParam(UriInfo uriInfo, String param, long defaultValue) {
        String value = uriInfo.getQueryParameters().getFirst(param);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
