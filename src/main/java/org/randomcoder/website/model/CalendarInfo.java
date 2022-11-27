package org.randomcoder.website.model;

import jakarta.ws.rs.core.UriInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CalendarInfo {

    private static final Set<String> REMOVED_PARAMS = Set.of(
            "month", "day", "year", "page.page", "page.size", "page.sort");

    private final String selfLink;
    private final String prevMonthLink;
    private final String nextMonthLink;
    private final String displayedMonthText;
    private final List<Week> weeks;

    public CalendarInfo(UriInfo uriInfo, boolean[] daysWithContent) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH);
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        String sYear = uriInfo.getQueryParameters().getFirst("year");
        if (sYear != null) {
            cal.set(Calendar.YEAR, Integer.parseInt(sYear));
        }
        String sMonth = uriInfo.getQueryParameters().getFirst("month");
        if (sMonth != null) {
            cal.set(Calendar.MONTH, Integer.parseInt(sMonth) - 1);
        }
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date selectedDate = cal.getTime();

        // sanity check; don't allow prev link if date is more than 10 years in the past
        Calendar prevCal = new GregorianCalendar();
        prevCal.setTime(new Date());
        prevCal.add(Calendar.YEAR, -10);
        Date prevLimit = prevCal.getTime();

        // sanity check; don't allow next link if date is more than 1 year in the future
        Calendar nextCal = new GregorianCalendar();
        nextCal.setTime(new Date());
        nextCal.add(Calendar.YEAR, 1);
        Date nextLimit = nextCal.getTime();

        displayedMonthText = new SimpleDateFormat("MMM yyyy").format(selectedDate);

        selfLink = makeLink(urlWithParams(uriInfo, REMOVED_PARAMS, Collections.emptyMap()));
        prevMonthLink = selectedDate.before(prevLimit)
                ? null
                : makeLink(urlWithParams(uriInfo, REMOVED_PARAMS, prevMonthParams(selectedDate)));
        nextMonthLink = selectedDate.after(nextLimit)
                ? null
                : makeLink(urlWithParams(uriInfo, REMOVED_PARAMS, nextMonthParams(selectedDate)));

        // capture current month / year
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        int yearMonth = year * 100 + month;

        // rewind calendar until first day is sunday
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            cal.add(Calendar.DATE, -1);
        }

        // build weeks
        SimpleDateFormat titleFormat = new SimpleDateFormat("MMMM d, yyyy");
        weeks = new ArrayList<>();

        // for first week, go forward until we hit the first day of the month
        {
            while ((cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH)) <= yearMonth) {
                // more weeks are needed
                Week week = new Week();
                while (week.getDays().size() < 7) {
                    if (cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) == yearMonth) {
                        boolean today = cal.get(Calendar.YEAR) == currentYear
                                && cal.get(Calendar.MONTH) == currentMonth
                                && cal.get(Calendar.DAY_OF_MONTH) == currentDay;

                        String link = null;
                        if (daysWithContent[cal.get(Calendar.DAY_OF_MONTH) - 1]) {
                            // content exists, generate link
                            link = makeLink(urlWithParams(uriInfo, REMOVED_PARAMS, currentDayParams(cal.getTime())));
                        }
                        week.getDays().add(new Day(cal.get(Calendar.DAY_OF_MONTH), link, titleFormat.format(cal.getTime()), today));
                    } else {
                        week.getDays().add(new Day(0, null, null, false));
                    }
                    cal.add(Calendar.DATE, 1);
                }
                weeks.add(week);
            }
        }

    }

    private static String makeLink(URL url) {
        return url.getPath() + ((url.getQuery() == null) ? "" : url.getQuery());
    }

    private static Map<String, String> currentDayParams(Date date) {
        Map<String, String> params = new HashMap<>();
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        params.put("month", Integer.toString(cal.get(Calendar.MONTH) + 1));
        params.put("year", Integer.toString(cal.get(Calendar.YEAR)));
        params.put("day", Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
        return params;
    }

    private static Map<String, String> prevMonthParams(Date date) {
        Map<String, String> params = new HashMap<>();
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        params.put("month", Integer.toString(cal.get(Calendar.MONTH) + 1));
        params.put("year", Integer.toString(cal.get(Calendar.YEAR)));
        return params;
    }

    private static Map<String, String> nextMonthParams(Date date) {
        Map<String, String> params = new HashMap<>();
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        params.put("month", Integer.toString(cal.get(Calendar.MONTH) + 1));
        params.put("year", Integer.toString(cal.get(Calendar.YEAR)));
        return params;
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

    public String getSelfLink() {
        return selfLink;
    }

    public String getPrevMonthLink() {
        return prevMonthLink;
    }

    public String getNextMonthLink() {
        return nextMonthLink;
    }

    public String getDisplayedMonthText() {
        return displayedMonthText;
    }

    public List<Week> getWeeks() {
        return weeks;
    }

    public static class Week {
        private final List<Day> days = new ArrayList<>();

        public List<Day> getDays() {
            return days;
        }
    }

    public static class Day {
        private final int number;
        private final String link;
        private final String linkTitle;
        private final boolean today;

        public Day(int number, String link, String linkTitle, boolean today) {
            this.number = number;
            this.link = link;
            this.linkTitle = linkTitle;
            this.today = today;
        }

        public boolean isToday() {
            return today;
        }

        public int getNumber() {
            return number;
        }

        public String getLink() {
            return link;
        }

        public String getLinkTitle() {
            return linkTitle;
        }

        public boolean isVisible() {
            return (number > 0);
        }
    }

}
