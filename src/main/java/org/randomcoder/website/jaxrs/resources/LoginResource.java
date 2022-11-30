package org.randomcoder.website.jaxrs.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.randomcoder.website.thymeleaf.ThymeleafEntity;

import java.net.URI;

@Path("")
@PermitAll
public class LoginResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("login")
    public ThymeleafEntity login() {
        return new ThymeleafEntity("login");
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("login-error")
    public ThymeleafEntity loginError() {
        return new ThymeleafEntity("login-error");
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("login-submit")
    public Response loginSubmit(
            @FormParam("j_username") String username,
            @FormParam("j_password") String password,
            @FormParam("remember-me") @DefaultValue("false") boolean rememberMe) {

        // simulate failure
        return Response
                .status(Response.Status.FOUND)
                .location(URI.create("/login-error"))
                .build();
    }

}