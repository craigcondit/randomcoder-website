package org.randomcoder.website.bo;

import java.util.Date;
import java.util.List;

@FunctionalInterface
public interface BeforeDatePageGenerator<T> {

    List<T> generate(Date endDate, long offset, long length);

}
