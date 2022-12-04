package org.randomcoder.website.jaxrs.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.randomcoder.website.Config;
import org.randomcoder.website.bo.TagBusiness;
import org.randomcoder.website.command.TagAddCommand;
import org.randomcoder.website.command.TagEditCommand;
import org.randomcoder.website.model.PageUtils;
import org.randomcoder.website.model.PagerInfo;
import org.randomcoder.website.model.Roles;
import org.randomcoder.website.thymeleaf.ThymeleafEntity;
import org.randomcoder.website.validation.TagAddValidator;
import org.randomcoder.website.validation.TagEditValidator;
import org.randomcoder.website.validation.ValidatorContext;

import java.net.URI;
import java.util.HashMap;

@Singleton
@RolesAllowed(Roles.MANAGE_TAGS)
@Path("")
public class TagResource {

    @Inject
    @Named(Config.TAG_PAGESIZE_MAX)
    int maximumPageSize = 100;

    @Inject
    TagBusiness tagBusiness;

    @Inject
    TagAddValidator tagAddValidator;

    @Inject
    TagEditValidator tagEditValidator;

    @Inject
    UriInfo uriInfo;

    @Inject
    HttpHeaders headers;

    @GET
    @Path("tag")
    public ThymeleafEntity listTags() {
        var oal = PageUtils.parsePagination(uriInfo, 25, maximumPageSize);
        var tags = tagBusiness.findTagStatistics(oal.offset(), oal.length());

        return new ThymeleafEntity("tag-list")
                .withVariable("tags", tags)
                .withVariable("pagerInfo", new PagerInfo<>(tags, uriInfo));

    }

    @GET
    @Path("tag/add")
    public ThymeleafEntity addTag() {
        var command = new TagAddCommand();

        return new ThymeleafEntity("tag-add")
                .withVariable("command", command)
                .withVariable("errors", new HashMap<>());
    }

    @POST
    @Path("tag/add")
    public Response addTagSubmit(
            @BeanParam TagAddCommand command,
            @FormParam("cancel") String cancel) {

        if (cancel != null) {
            return Response.status(Response.Status.FOUND)
                    .location(URI.create("/tag"))
                    .build();
        }

        var context = new ValidatorContext(headers.getLanguage());
        tagAddValidator.validate(context, command);
        var errors = context.getErrors();
        if (!errors.isEmpty()) {
            return Response.ok(new ThymeleafEntity("tag-add")
                            .withVariable("command", command)
                            .withVariable("errors", errors))
                    .build();
        }

        tagBusiness.createTag(command);

        return Response.status(Response.Status.FOUND)
                .location(URI.create("/tag"))
                .build();
    }

    @GET
    @Path("tag/edit")
    public ThymeleafEntity editTag(@QueryParam("id") long id) {
        var command = new TagEditCommand();
        tagBusiness.loadTagForEditing(command::load, id);

        return new ThymeleafEntity("tag-edit")
                .withVariable("command", command)
                .withVariable("errors", new HashMap<>());
    }

    @POST
    @Path("tag/edit")
    public Response editTagSubmit(
            @BeanParam TagEditCommand command,
            @FormParam("cancel") String cancel) {

        if (cancel != null) {
            return Response.status(Response.Status.FOUND)
                    .location(URI.create("/tag"))
                    .build();
        }

        var context = new ValidatorContext(headers.getLanguage());
        tagEditValidator.validate(context, command);
        var errors = context.getErrors();
        if (!errors.isEmpty()) {
            return Response.ok(new ThymeleafEntity("tag-edit")
                            .withVariable("command", command)
                            .withVariable("errors", errors))
                    .build();
        }

        tagBusiness.updateTag(command, command.getId());

        return Response.status(Response.Status.FOUND)
                .location(URI.create("/tag"))
                .build();
    }

    @POST
    @Path("tag/delete")
    public Response deleteTagSubmit(@FormParam("id") long id) {
        tagBusiness.deleteTag(id);

        return Response.status(Response.Status.FOUND)
                .location(URI.create("/tag"))
                .build();
    }

}