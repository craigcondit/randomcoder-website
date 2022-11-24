package org.randomcoder.mvc.command;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Command class used for article paging.
 */
public class ArticleListCommand implements Serializable {
    private static final long serialVersionUID = -366354426204104148L;

    private int month = -1;
    private int day = -1;
    private int year = -1;

    /**
     * Gets the month to display results for.
     *
     * @return month number
     */
    public int getMonth() {
        return month;
    }

    /**
     * Sets the month to display results for.
     *
     * @param month month number
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * Gets the day of month to display results for.
     *
     * @return day of month
     */
    public int getDay() {
        return day;
    }

    /**
     * Sets the day of month to display results for.
     *
     * @param day day of month
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * Gets the year to display results for.
     *
     * @return year
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets the year to display results for.
     *
     * @param year year
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Gets a string representation of this object, suitable for debugging.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return ReflectionToStringBuilder
                .toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
