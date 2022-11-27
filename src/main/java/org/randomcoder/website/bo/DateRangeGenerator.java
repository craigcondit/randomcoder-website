package org.randomcoder.website.bo;

import java.util.Date;
import java.util.List;

@FunctionalInterface
public interface DateRangeGenerator<T> {

    List<T> generate(Date startDate, Date endDate);

}
