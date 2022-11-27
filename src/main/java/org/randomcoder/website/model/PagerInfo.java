package org.randomcoder.website.model;

import jakarta.ws.rs.core.UriInfo;
import org.randomcoder.website.data.Page;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PagerInfo<T> {

    private static final Set<String> REMOVED_PARAMS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("page.page", "page.size")));

    private final List<PageLink> links;

    public PagerInfo(Page<T> page, UriInfo uriInfo) {
        this.links = new ArrayList<>();
        long size = page.getPageSize();

        if (!page.isFirst()) {
            // generate link to prev page
            String link = makeLink(urlWithParams(uriInfo, REMOVED_PARAMS, pageParams(page.getPageNumber() - 1, size)));
            String text = "&#171;";
            links.add(new PageLink(text, link));
        }

        // do previous pages
        long startPage = Math.max(0, page.getNumber() - 10);
        for (long i = startPage; i < page.getNumber(); i++) {
            String link = makeLink(urlWithParams(uriInfo, REMOVED_PARAMS, pageParams(i, size)));
            links.add(new PageLink(Long.toString(i + 1), link));
        }

        // add current page
        links.add(new PageLink(Long.toString(page.getPageNumber() + 1), null));

        // do next pages
        long lastPage = Math.min(page.getTotalPages() - 1, page.getPageNumber() + 10);
        for (long i = page.getPageNumber() + 1; i <= lastPage; i++) {
            String link = makeLink(urlWithParams(uriInfo, REMOVED_PARAMS, pageParams(i, size)));
            links.add(new PageLink(Long.toString(i + 1), link));
        }

        if (!page.isLast()) {
            // generate link to next page
            String link = makeLink(urlWithParams(uriInfo, REMOVED_PARAMS, pageParams(page.getNumber() + 1, size)));
            String text = "&#187;";
            links.add(new PageLink(text, link));
        }
    }

    private static Map<String, String> pageParams(long pageNumber, long pageSize) {
        Map<String, String> map = new HashMap<>();
        map.put("page.page", Long.toString(pageNumber));
        map.put("page.size", Long.toString(pageSize));
        return map;
    }

    private static String makeLink(URL url) {
        return url.getPath() + ((url.getQuery() == null) ? "" : url.getQuery());
    }

    private static URL urlWithParams(UriInfo uriInfo, Set<String> removedParams, Map<String, String> addedParams) {
        try {
            URL url = uriInfo.getRequestUri().toURL();
            String query = buildQuery(uriInfo, removedParams, addedParams);
            url = new URL(url, url.getPath() + (query == null ? "" : "?" + query));
            return url;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unexpected error while creating calendar info", e);
        }

    }

    private static String buildQuery(UriInfo uriInfo, Set<String> removedParams, Map<String, String> addedParams) {
        StringBuilder buf = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            String key = entry.getKey();
            if (removedParams.contains(key)) {
                continue;
            }
            if (addedParams.keySet().contains(key)) {
                continue;
            }
            for (String value : entry.getValue()) {
                buf.append((buf.length() == 0) ? "?" : "&");
                buf.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
                buf.append("=");
                buf.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            }
        }

        for (Map.Entry<String, String> entry : addedParams.entrySet()) {
            buf.append((buf.length() == 0) ? "?" : "&");
            buf.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            buf.append("=");
            buf.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return buf.toString();
    }

    public List<PageLink> getLinks() {
        return links;
    }

    public static class PageLink {
        private final String text;
        private final String link;

        public PageLink(String text, String link) {
            this.text = text;
            this.link = link;
        }

        public String getText() {
            return text;
        }

        public String getLink() {
            return link;
        }
    }

}
