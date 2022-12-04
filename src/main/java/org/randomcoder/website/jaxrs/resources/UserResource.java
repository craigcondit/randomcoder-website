package org.randomcoder.website.jaxrs.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.inject.Named;
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
import jakarta.ws.rs.core.UriInfo;
import org.randomcoder.website.Config;
import org.randomcoder.website.bo.UserBusiness;
import org.randomcoder.website.command.ChangePasswordCommand;
import org.randomcoder.website.command.UserAddCommand;
import org.randomcoder.website.command.UserProfileCommand;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.User;
import org.randomcoder.website.model.PageUtils;
import org.randomcoder.website.model.PagerInfo;
import org.randomcoder.website.model.Roles;
import org.randomcoder.website.thymeleaf.ThymeleafEntity;
import org.randomcoder.website.validation.ChangePasswordValidator;
import org.randomcoder.website.validation.UserAddValidator;
import org.randomcoder.website.validation.UserEditValidator;
import org.randomcoder.website.validation.UserProfileValidator;
import org.randomcoder.website.validation.ValidatorContext;

import java.net.URI;
import java.util.HashMap;

@Singleton
@RolesAllowed("*")
@Path("")
public class UserResource {

    @Inject
    UserBusiness userBusiness;

    @Inject
    UserProfileValidator userProfileValidator;

    @Inject
    ChangePasswordValidator changePasswordValidator;

    @Inject
    UserAddValidator userAddValidator;

    @Inject
    UserEditValidator userEditValidator;

    @Inject
    SecurityContext securityContext;

    @Inject
    HttpHeaders headers;

    @Inject
    UriInfo uriInfo;

    @Inject
    @Named(Config.USER_PAGESIZE_MAX)
    int userPageSizeMax = 100;

    @GET
    @Path("user/profile")
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
    @Path("user/profile")
    @Produces(MediaType.TEXT_HTML)
    public Response userProfileSubmit(
            @BeanParam UserProfileCommand command,
            @FormParam("cancel") String cancel) {

        if (cancel == null) {
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
    @Path("user/profile/change-password")
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
    @Path("user/profile/change-password")
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

    @GET
    @RolesAllowed(Roles.MANAGE_USERS)
    @Path("user")
    public ThymeleafEntity listUsers() {

        var oal = PageUtils.parsePagination(uriInfo, 25, userPageSizeMax);
        long offset = oal.offset();
        long length = oal.length();

        Page<User> users = userBusiness.findAll(offset, length);

        return new ThymeleafEntity("user-list")
                .withVariable("users", users)
                .withVariable("pagerInfo", new PagerInfo<>(users, uriInfo));
    }

    @GET
    @Path("user/add")
    @RolesAllowed(Roles.MANAGE_USERS)
    public ThymeleafEntity addUser() {
        var command = new UserAddCommand();
        command.setEnabled(true);

        return new ThymeleafEntity("user-add")
                .withVariable("command", command)
                .withVariable("errors", new HashMap<>())
                .withVariable("availableRoles", userBusiness.listRoles());
    }

    @POST
    @Path("user/add")
    @RolesAllowed(Roles.MANAGE_USERS)
    public Response addUserSubmit(@BeanParam UserAddCommand command) {
        ValidatorContext context = new ValidatorContext(headers.getLanguage());
        userAddValidator.validate(context, command);
        var errors = context.getErrors();
        if (!errors.isEmpty()) {
            return Response.ok(new ThymeleafEntity("user-add")
                            .withVariable("command", command)
                            .withVariable("errors", errors)
                            .withVariable("availableRoles", userBusiness.listRoles()))
                    .build();
        }

        userBusiness.createUser(command);

        return Response.status(Response.Status.FOUND)
                .location(URI.create("/user"))
                .build();
    }

}
