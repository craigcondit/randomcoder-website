package org.randomcoder.website.jaxrs.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.randomcoder.website.bo.ArticleBusiness;
import org.randomcoder.website.data.ModerationException;
import org.randomcoder.website.model.Roles;

import java.net.URI;

@Path("/comment")
@RolesAllowed(Roles.MANAGE_COMMENTS)
public class CommentResource {

    @Inject
    ArticleBusiness articleBusiness;

    @PUT
    @Path("{id}/approve")
    public void approveComment(@PathParam("id") long id) throws ModerationException {
        articleBusiness.approveComment(id);
    }

    @DELETE
    @Path("{id}/approve")
    public void disapproveComment(@PathParam("id") long id) throws ModerationException {
        articleBusiness.disapproveComment(id);
    }

    @POST
    @Path("{id}/approve")
    public Response manageCommentBrowser(
            @PathParam("id") long id,
            @FormParam("_verb") String verb) throws ModerationException {

        var article = switch (verb) {
            case "PUT" -> articleBusiness.approveComment(id);
            case "DELETE" -> articleBusiness.disapproveComment(id);
            default -> throw new BadRequestException();
        };

        return Response
                .status(Response.Status.FOUND)
                .location(URI.create(article.getPermalinkUrl()))
                .build();
    }

    @DELETE
    @Path("{id}")
    public void deleteComment(@PathParam("id") long id) {
        articleBusiness.deleteComment(id);
    }

    @POST
    @Path("{id}/delete")
    public Response deleteCommentBrowser(@PathParam("id") long id, @FormParam("_verb") String verb) {
        var article = switch (verb) {
            case "DELETE" -> articleBusiness.deleteComment(id);
            default -> throw new BadRequestException();
        };
        return Response
                .status(Response.Status.FOUND)
                .location(URI.create(article.getPermalinkUrl()))
                .build();
    }

}
