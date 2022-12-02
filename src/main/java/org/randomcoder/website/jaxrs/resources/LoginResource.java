package org.randomcoder.website.jaxrs.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.randomcoder.db.User;
import org.randomcoder.website.bo.UserBusiness;
import org.randomcoder.website.jaxrs.features.SecurityFeature;
import org.randomcoder.website.thymeleaf.ThymeleafEntity;

import java.net.URI;
import java.time.Duration;
import java.util.Date;

@Path("")
@PermitAll
public class LoginResource {

    private static final long TOKEN_EXPIRATION_MS = Duration.ofHours(25).toSeconds() * 1000;

    @Inject
    UserBusiness userBusiness;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("login")
    public Response login() {
        return Response.ok(new ThymeleafEntity("login"))
                .build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("login-error")
    public Response loginError() {
        return Response.ok(new ThymeleafEntity("login-error"))
                .build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("logout")
    public Response logout() {
        return Response.status(Response.Status.FOUND)
                .cookie(clearAuthCookies())
                .location(URI.create("/"))
                .build();
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("logout")
    public Response logoutPost() {
        return Response.status(Response.Status.FOUND)
                .cookie(clearAuthCookies())
                .location(URI.create("/"))
                .build();
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("login-submit")
    public Response loginSubmit(
            @CookieParam(SecurityFeature.REDIRECT_COOKIE) @DefaultValue("") String redirectUri,
            @FormParam("j_username") String username,
            @FormParam("j_password") String password,
            @FormParam("remember-me") @DefaultValue("false") boolean rememberMe) {

        var user = userBusiness.findUserByNameEnabled(username);
        if (user == null || !User.hashPassword(password).equals(user.getPassword())) {
            return Response
                    .status(Response.Status.FOUND)
                    .location(URI.create("/login-error"))
                    .build();
        }

        // remove redirect cookie
        var redirectCookie = new NewCookie
                .Builder(SecurityFeature.REDIRECT_COOKIE)
                .value("")
                .expiry(new Date(0))
                .httpOnly(true)
                .build();

        // generate token and store to session
        String token = userBusiness.generateAuthToken(user);

        var authCookie = new NewCookie
                .Builder(SecurityFeature.AUTH_COOKIE)
                .value(token)
                .expiry(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MS))
                .httpOnly(true)
                .build();

        // TODO handle remember-me authentication
        // When using this, we should use a site-wide key with a longer expiration, rather than the per-instance key

        // set redirect URL to saved, or site root if not
        URI location = "".equals(redirectUri) ? URI.create("/") : URI.create(redirectUri);

        return Response.status(Response.Status.FOUND)
                .cookie(redirectCookie, authCookie)
                .location(location)
                .build();
    }

    static NewCookie[] clearAuthCookies() {
        return new NewCookie[]{
                new NewCookie
                        .Builder(SecurityFeature.REDIRECT_COOKIE)
                        .value("")
                        .expiry(new Date(0))
                        .httpOnly(true)
                        .build(),
                new NewCookie
                        .Builder(SecurityFeature.AUTH_COOKIE)
                        .value("")
                        .expiry(new Date(0))
                        .httpOnly(true)
                        .build()};
    }

}