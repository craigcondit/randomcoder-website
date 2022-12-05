package org.randomcoder.website.bo;

import org.randomcoder.website.data.User;

public record UserAuthentication(User user, long loginTime, long verifyTime, boolean rememberMe) {
}
