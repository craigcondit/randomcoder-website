package org.randomcoder.website.bo;

import java.time.Instant;

public interface AppInfoBusiness {

    String getApplicationName();

    String getApplicationVersion();

    Instant getBuildDate();

}