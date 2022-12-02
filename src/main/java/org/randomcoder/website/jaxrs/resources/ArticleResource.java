package org.randomcoder.website.jaxrs.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import org.randomcoder.website.bo.ArticleBusiness;
import org.randomcoder.website.bo.TagBusiness;
import org.randomcoder.website.command.CommentCommand;
import org.randomcoder.website.controller.ArticleController;
import org.randomcoder.website.controller.ArticleTagListController;
import org.randomcoder.website.controller.HomeController;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.Tag;
import org.randomcoder.website.thymeleaf.ThymeleafEntity;
import org.randomcoder.website.validation.CommentValidator;
import org.randomcoder.website.validation.ValidatorContext;

import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Path("")
@PermitAll
public class ArticleResource {

    private static final String REFERER = "Referer";

    @Inject
    HomeController homeController;

    @Inject
    ArticleTagListController articleTagListController;

    @Inject
    ArticleController articleController;

    @Inject
    TagBusiness tagBusiness;

    @Inject
    ArticleBusiness articleBusiness;

    @Inject
    CommentValidator commentValidator;

    @Inject
    UriInfo uriInfo;

    @Inject
    SecurityContext securityContext;

    @Inject
    HttpHeaders headers;

    @Inject
    HttpServletRequest request;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public ThymeleafEntity home() {
        return new ThymeleafEntity("home").withVariables(homeController.buildModel(uriInfo));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Page<Article> articlesApi(
            @QueryParam("offset") @DefaultValue("0") long offset,
            @QueryParam("length") @DefaultValue("50") long length) {

        var cutoffDate = new Date(Instant.now().plus(31, ChronoUnit.DAYS).toEpochMilli());
        return articleBusiness.listArticlesBeforeDate(cutoffDate, offset, length);
    }

    @GET
    @Path("/tags/{tagName}")
    @Produces(MediaType.TEXT_HTML)
    public ThymeleafEntity articlesByTag() {
        return new ThymeleafEntity("article-tag-list").withVariables(articleTagListController.buildModel(uriInfo));
    }

    @GET
    @Path("/tags/{tagName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Page<Article> articlesByTagApi(
            @PathParam("tagName") String tagName,
            @QueryParam("offset") @DefaultValue("0") long offset,
            @QueryParam("length") @DefaultValue("50") long length) {

        Tag tag = tagBusiness.findTagByName(tagName);
        if (tag == null) {
            throw new NotFoundException();
        }

        var cutoffDate = new Date(Instant.now().plus(31, ChronoUnit.DAYS).toEpochMilli());
        return articleBusiness.listArticlesByTagBeforeDate(tag, cutoffDate, offset, length);
    }

    @GET
    @Path("/articles/id/{id}")
    @Produces(MediaType.TEXT_HTML)
    public ThymeleafEntity articleById(@PathParam("id") long id) {
        return articleInternal(articleBusiness.readArticle(id));
    }

    @GET
    @Path("/articles/{permalink}")
    @Produces(MediaType.TEXT_HTML)
    public ThymeleafEntity articleByPermalink(@PathParam("permalink") String permalink) {
        return articleInternal(articleBusiness.findArticleByPermalink(permalink));
    }

    private ThymeleafEntity articleInternal(Article article) {
        if (article == null) {
            throw new NotFoundException();
        }

        CommentCommand command = new CommentCommand();
        command.setAnonymous(securityContext.getUserPrincipal() == null);

        return new ThymeleafEntity("article-view").withVariables(articleController.buildModel(command, article));
    }

    @POST
    @Path("/articles/id/{id}")
    @Produces(MediaType.TEXT_HTML)
    public Response commentById(@PathParam("id") long id, @BeanParam CommentCommand command) {
        return commentInternal(articleBusiness.readArticle(id), command);
    }

    @POST
    @Path("/articles/{permalink}")
    @Produces(MediaType.TEXT_HTML)
    public Response commentByPermalink(@PathParam("permalink") String permalink, @BeanParam CommentCommand command) {
        return commentInternal(articleBusiness.findArticleByPermalink(permalink), command);
    }

    private Response commentInternal(Article article, CommentCommand command) {
        if (article == null) {
            throw new NotFoundException();
        }

        command.setAnonymous(securityContext.getUserPrincipal() == null);

        var validatorContext = new ValidatorContext(headers.getLanguage());

        commentValidator.validate(validatorContext, command);
        var errors = validatorContext.getErrors();

        if (!errors.isEmpty()) {
            return Response.ok(new ThymeleafEntity("article-view")
                            .withVariables(articleController.buildModel(command, article, errors)))
                    .build();
        }

        String username = Optional
                .ofNullable(securityContext.getUserPrincipal())
                .map(Principal::getName)
                .orElse(null);

        String referrer = headers.getHeaderString(REFERER);
        String ipAddress = request.getRemoteHost();
        String userAgent = headers.getHeaderString(HttpHeaders.USER_AGENT);

        articleBusiness.createComment(command, article.getId(), username, referrer, ipAddress, userAgent);

        return Response.status(Response.Status.FOUND)
                .location(uriInfo.getRequestUri())
                .build();
    }

}
