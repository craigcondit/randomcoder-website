package org.randomcoder.website.jaxrs.util;

import jakarta.ws.rs.core.NewCookie;

import java.util.Date;

public final class CookieUtils {

    private CookieUtils() {
    }

    public static NewCookie sessionCookie(String domain, String name, String value, boolean secure) {
        var builder = new NewCookie.Builder(name)
                .httpOnly(true)
                .domain(domain)
                .sameSite(NewCookie.SameSite.STRICT)
                .secure(secure)
                .path("/");

        if (value == null) {
            return builder.value("").expiry(new Date(0L)).build();
        }

        return builder.value(value).build();
    }

    public static NewCookie persistentCookie(String domain, String name, String value, Date expiry, boolean secure) {
        var builder = new NewCookie.Builder(name)
                .httpOnly(true)
                .domain(domain)
                .sameSite(NewCookie.SameSite.STRICT)
                .secure(secure)
                .path("/");

        if (value == null) {
            return builder.value("").expiry(new Date(0L)).build();
        }

        return builder.value(value).expiry(expiry).build();
    }

}
