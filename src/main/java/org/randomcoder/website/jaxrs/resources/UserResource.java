package org.randomcoder.website.jaxrs.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.randomcoder.website.bo.UserBusiness;
import org.randomcoder.website.command.ChangePasswordCommand;
import org.randomcoder.website.command.UserProfileCommand;
import org.randomcoder.website.data.User;
import org.randomcoder.website.thymeleaf.ThymeleafEntity;
import org.randomcoder.website.validation.ChangePasswordValidator;
import org.randomcoder.website.validation.UserProfileValidator;
import org.randomcoder.website.validation.ValidatorContext;

import java.net.URI;
import java.util.HashMap;

@Singleton
@RolesAllowed("*")
@Path("/user")
public class UserResource {

    @Inject
    UserBusiness userBusiness;

    @Inject
    UserProfileValidator userProfileValidator;

    @Inject
    ChangePasswordValidator changePasswordValidator;

    @Inject
    SecurityContext securityContext;

    @Inject
    HttpHeaders headers;

    @GET
    @Path("profile")
    @Produces(MediaType.TEXT_HTML)
    public ThymeleafEntity userProfile() {
        User user = userBusiness.findUserByName(securityContext.getUserPrincipal().getName());
        var command = new UserProfileCommand();
        command.setEmailAddress(user.getEmailAddress());
        command.setWebsite(user.getWebsite());
        return new ThymeleafEntity("user-profile")
                .withVariable("user", user)
                .withVariable("command", command)
                .withVariable("errors", new HashMap<>());
    }

    @POST
    @Path("profile")
    @Produces(MediaType.TEXT_HTML)
    public Response userProfileSubmit(
            @BeanParam UserProfileCommand command,
            @FormParam("cancel") @DefaultValue("") String cancel) {

        if (!"".equals(cancel)) {
            return Response.status(Response.Status.FOUND).location(URI.create("/")).build();
        }

        User user = userBusiness.findUserByName(securityContext.getUserPrincipal().getName());

        ValidatorContext context = new ValidatorContext(headers.getLanguage());
        userProfileValidator.validate(context, command);
        var errors = context.getErrors();
        if (!errors.isEmpty()) {
            return Response.ok(new ThymeleafEntity("user-profile")
                            .withVariable("user", user)
                            .withVariable("command", command)
                            .withVariable("errors", errors))
                    .build();
        }

        userBusiness.updateUser(command, user.getId());

        return Response.status(Response.Status.FOUND).location(URI.create("/")).build();
    }

    @GET
    @Path("profile/change-password")
    @Produces(MediaType.TEXT_HTML)
    public ThymeleafEntity changePassword() {
        User user = userBusiness.findUserByNameEnabled(securityContext.getUserPrincipal().getName());

        ChangePasswordCommand command = new ChangePasswordCommand();
        command.setUser(user);
        return new ThymeleafEntity("change-password")
                .withVariable("command", command)
                .withVariable("errors", new HashMap<>());
    }

    @POST
    @Path("profile/change-password")
    @Produces(MediaType.TEXT_HTML)
    public Response changePasswordSubmit(
            @BeanParam ChangePasswordCommand command, @FormParam("cancel") @DefaultValue("") String cancel) {

        if (!"".equals(cancel)) {
            return Response.status(Response.Status.FOUND).location(URI.create("/")).build();
        }

        User user = userBusiness.findUserByName(securityContext.getUserPrincipal().getName());
        command.setUser(user);

        ValidatorContext context = new ValidatorContext(headers.getLanguage());
        changePasswordValidator.validate(context, command);

        var errors = context.getErrors();
        if (!errors.isEmpty()) {
            return Response.ok(new ThymeleafEntity("change-password")
                            .withVariable("command", command)
                            .withVariable("errors", errors))
                    .build();
        }

        userBusiness.changePassword(user.getUserName(), command.getPassword());

        return Response.status(Response.Status.FOUND).location(URI.create("/user/profile")).build();
    }

}
