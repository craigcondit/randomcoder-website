package org.randomcoder.website.jaxrs.providers;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.randomcoder.website.bo.UserAuthentication;
import org.randomcoder.website.bo.UserBusiness;
import org.randomcoder.website.data.User;
import org.randomcoder.website.data.UserSecurityContext;
import org.randomcoder.website.jaxrs.features.SecurityFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Provider
@Priority(Priorities.AUTHENTICATION)
@Singleton
public class SecurityFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    private static final long MAX_AUTH_AGE = Duration.ofHours(1).toSeconds() * 1000;
    private static final long TOKEN_SLEW_MS = Duration.ofMinutes(5).toSeconds() * 1000;

    private final ConcurrentMap<String, UserAuthentication> authCache = new ConcurrentHashMap<>();

    private ScheduledExecutorService executor;

    @Inject
    public UserBusiness userBusiness;

    @PostConstruct
    public void start() {
        logger.info("Starting security filter...");
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(this::removeExpiredTokens, 5, 5, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void stop() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        var authCookie = context.getCookies().get(SecurityFeature.AUTH_COOKIE);
        if (authCookie == null) {
            // not logged in
            return;
        }

        var token = authCookie.getValue();

        var auth = authCache.get(token);
        if (!validAuth(auth)) {
            authCache.remove(token);

            auth = userBusiness.validateAuthToken(token);
            if (auth == null) {
                // token didn't validate
                return;
            }
            authCache.put(token, auth);
        }

        User user = auth.user();

        boolean secure = context.getSecurityContext().isSecure();

        context.setSecurityContext(new UserSecurityContext(user, secure));
    }

    public void removeExpiredTokens() {
        logger.info("Removing expired tokens...");
        int expiredCount = 0;
        int totalCount = 0;

        for (var it = authCache.entrySet().iterator(); it.hasNext();) {
            var entry = it.next();
            totalCount++;
            if (!validAuth(entry.getValue())) {
                it.remove();
                expiredCount++;
            }
        }

        logger.info("Removed {} expired of {} total auth tokens", expiredCount, totalCount);
    }

    static boolean validAuth(UserAuthentication auth) {
        if (auth == null) {
            return false;
        }

        // make sure we can still trust credentials
        long authAgeMs = System.currentTimeMillis() - auth.verifyTime();

        // allow past up to TOKEN_SLEW_MS and future up to MAX_AUTH_AGE
        return authAgeMs > -TOKEN_SLEW_MS && authAgeMs < MAX_AUTH_AGE;
    }

}
