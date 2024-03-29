package org.randomcoder.website.validation;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public final class DataValidationUtils {
    private static final Logger logger = LoggerFactory.getLogger(DataValidationUtils.class);

    private DataValidationUtils() {
    }

    public static String canonicalizeDomainName(String domain) {
        if (domain == null) {
            return null;
        }
        domain = domain.toLowerCase(Locale.US).trim();
        if (domain.length() == 0) {
            return null;
        }
        return domain;
    }

    public static boolean isValidDomainName(String domain) {
        domain = canonicalizeDomainName(domain);
        if (domain == null) {
            return false;
        }
        if (domain.length() > 255) {
            return false;
        }

        String dom = "([a-z0-9]+|([a-z0-9]+[a-z0-9\\-]*[a-z0-9]+))";
        if (!domain.matches("^(" + dom + "\\.)+" + dom + "+$")) {
            return false;
        }

        String[] parts = domain.split("\\.");
        for (String part : parts) {
            if (part.length() > 67) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidDomainWildcard(String domain) {
        domain = canonicalizeDomainName(domain);
        if (domain == null) {
            return false;
        }
        if (domain.length() > 255) {
            return false;
        }

        if (domain.startsWith("*.")) {
            domain = domain.substring(2);
        }

        return isValidDomainName(domain);
    }

    public static boolean isValidLocalEmailAccount(String email) {
        if (!email.matches("^[A-Za-z0-9_\\-\\.]+$")) {
            return false;
        }
        return email.length() <= 64;
    }

    public static boolean isValidIpAddress(String ipAddress) {
        if (ipAddress == null) {
            return false;
        }
        ipAddress = ipAddress.trim();
        if (!ipAddress.matches("^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+$")) {
            return false;
        }
        String[] parts = ipAddress.split("\\.");
        for (int i = 0; i < 4; i++) {
            int value = Integer.parseInt(parts[i]);
            if (value < 0 || value > 255) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidUrl(String url) {
        return isValidUrl(url, "http", "https");
    }

    public static boolean isValidUrl(String url, String... allowedProtocols) {
        URL validated = null;

        try {
            validated = new URL(url);
        } catch (MalformedURLException e) {
            logger.debug("Malformed URL", e);
            return false;
        }

        String proto = validated.getProtocol();
        for (String allowed : allowedProtocols) {
            if (allowed.equals(proto)) {
                return true;
            }
        }

        return false;
    }

    public static String[] splitEmailAddress(String email) {
        String[] results = new String[]{null, null};
        if (email == null) {
            return results;
        }
        email = email.trim();

        // split into local-name@domain-name, taking into account escapes
        boolean quoted = false;
        int loc = -1;
        for (int i = 0; i < email.length(); i++) {
            char c = email.charAt(i);
            if (c == '\\') {
                i++;
                continue;
            }
            if (i == 0 && c == '\"') {
                quoted = true;
                continue;
            }
            if (c == '\"') {
                quoted = false;
                continue;
            }
            if (c == '@' && !quoted) {
                loc = i;
                break;
            }
        }

        if (loc >= 0) {
            results[0] = email.substring(0, loc);
            results[1] = email.substring(loc + 1);
        } else {
            results[0] = email;
            results[1] = null;
        }
        return results;
    }

    public static boolean isValidEmailAddress(String email) {
        return isValidEmailAddress(email, false, false, false);
    }

    public static boolean isValidEmailAddress(String email, boolean strict, boolean local, boolean wildcard) {
        String[] parts = splitEmailAddress(email);

        email = parts[0];
        if (email == null) {
            return false;
        }

        if (parts[1] == null) {
            // no domain specified
            if (!local) {
                return false;
            }
        } else {
            // validate domain
            if (wildcard) {
                if (!isValidDomainWildcard(parts[1]) && !isValidIpAddress(parts[1])) {
                    return false;
                }
            } else {
                if (!isValidDomainName(parts[1]) && !isValidIpAddress(parts[1])) {
                    return false;
                }
            }
        }

        // validate local-name

        if (strict && (email.contains(".") || email.contains("@"))) {
            return false;
        }

        if (email.matches("^\\\".+\\\"$")) {
            // quoted-string
            email = email.substring(1, email.length() - 1);
            for (int i = 0; i < email.length(); i++) {
                char c = email.charAt(i);

                if (c == '\\') {
                    // peek at next character
                    i++;
                    c = email.charAt(i);

                    if (c >= 1 && c <= 9) {
                        continue;
                    }
                    if (c == 11) {
                        continue;
                    }
                    if (c == 12) {
                        continue;
                    }
                    if (c >= 14 && c <= 127) {
                        continue;
                    }
                } else {
                    if (c >= 1 && c <= 8) {
                        continue;
                    }
                    if (c == 11) {
                        continue;
                    }
                    if (c == 12) {
                        continue;
                    }
                    if (c >= 14 && c <= 31) {
                        continue;
                    }
                    if (c >= 33 && c <= 90) {
                        continue;
                    }
                    if (c >= 94 && c <= 127) {
                        continue;
                    }
                }
                return false;
            }
            return true;
        }

        // replace \? with benign character to handle escapes
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < email.length(); i++) {
            char c = email.charAt(i);

            if (c == '\\') {
                // peek at next character
                i++;
                c = email.charAt(i);

                if (c >= 1 && c <= 9) {
                    buf.append("x");
                    continue;
                }
                if (c == 11) {
                    buf.append("x");
                    continue;
                }
                if (c == 12) {
                    buf.append("x");
                    continue;
                }
                if (c >= 14 && c <= 127) {
                    buf.append("x");
                    continue;
                }

                return false; // illegal escape
            }

            buf.append(c);
        }

        email = buf.toString();

        // atom = alphanumeric, plus various punctuation
        String atom =
                "[A-Za-z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]";

        // either an atom, or atom(.atom)*
        String pattern = "^(" + atom + ")+(\\.(" + atom + ")+)*$";

        return email.matches(pattern);
    }

    public static String canonicalizeTagName(String name) {
        if (name == null) {
            return null;
        }

        // lowercase
        name = name.toLowerCase(Locale.US);

        // convert anything that is not alphanumeric to empty
        name = name.replaceAll("[^a-z0-9]+", " ");

        // collapse all whitespace
        name = name.replaceAll("\\s+", " ");

        // trim leading and trailing space
        name = name.trim();

        // convert spaces to dash
        name = name.replaceAll(" ", "-");

        // convert back to null if empty
        name = StringUtils.trimToNull(name);

        return name;
    }

}
