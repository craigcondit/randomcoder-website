package org.randomcoder.pagination;

import org.springframework.data.domain.Page;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public class PagerInfo<T> {

  private static final Set<String> REMOVED_PARAMS =
          Collections.unmodifiableSet(new HashSet<>(Arrays.asList("page.page", "page.size")));

  private final List<PageLink> links;

  public PagerInfo(Page<T> page, HttpServletRequest request) {
    this.links = new ArrayList<>();

    int size = page.getSize();

    if (!page.isFirst()) {
      // generate link to prev page
      String link = makeLink(urlWithParams(request, REMOVED_PARAMS, pageParams(page.getNumber() - 1, size)));
      String text = "&#171;";
      links.add(new PageLink(text, link));
    }

    // do previous pages
    int startPage = Math.max(0, page.getNumber() - 10);
    for (int i = startPage; i < page.getNumber(); i++) {
      String link = makeLink(urlWithParams(request, REMOVED_PARAMS, pageParams(i, size)));
      links.add(new PageLink(Integer.toString(i + 1), link));
    }

    // add current page
    links.add(new PageLink(Integer.toString(page.getNumber() + 1), null));

    // do next pages
    int lastPage = Math.min(page.getTotalPages() - 1, page.getNumber() + 10);
    for (int i = page.getNumber() + 1; i <= lastPage; i++) {
      String link = makeLink(urlWithParams(request, REMOVED_PARAMS, pageParams(i, size)));
      links.add(new PageLink(Integer.toString(i + 1), link));
    }

    if (!page.isLast()) {
      // generate link to next page
      String link = makeLink(urlWithParams(request, REMOVED_PARAMS, pageParams(page.getNumber() + 1, size)));
      String text = "&#187;";
      links.add(new PageLink(text, link));
    }
  }

  private static Map<String, String> pageParams(int pageNumber, int pageSize) {
    Map<String, String> map = new HashMap<>();
    map.put("page.page", Integer.toString(pageNumber));
    map.put("page.size", Integer.toString(pageSize));
    return map;
  }

  private static String makeLink(URL url) {
    return url.getPath() + ((url.getQuery() == null) ? "" : url.getQuery());
  }

  private static URL urlWithParams(HttpServletRequest request, Set<String> removedParams, Map<String, String> addedParams) {
    try {
      URL url = new URL(request.getRequestURL().toString());
      String query = buildQuery(request, removedParams, addedParams);
      url = new URL(url, url.getPath() + (query == null ? "" : "?" + query));
      return url;
    } catch (MalformedURLException e) {
      throw new RuntimeException("Unexpected error while creating calendar info", e);
    }

  }

  private static String buildQuery(HttpServletRequest request, Set<String> removedParams, Map<String, String> addedParams) {
    try {
      StringBuilder buf = new StringBuilder();

      for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
        String key = entry.getKey();
        if (removedParams.contains(key)) {
          continue;
        }
        if (addedParams.keySet().contains(key)) {
          continue;
        }
        for (String value : entry.getValue()) {
          buf.append((buf.length() == 0) ? "?" : "&");
          buf.append(URLEncoder.encode(key, "UTF-8"));
          buf.append("=");
          buf.append(URLEncoder.encode(value, "UTF-8"));
        }
      }

      for (Map.Entry<String, String> entry : addedParams.entrySet()) {
        buf.append((buf.length() == 0) ? "?" : "&");
        buf.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
        buf.append("=");
        buf.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
      }
      return buf.toString();

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Unexpected error while building base params", e);
    }
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
