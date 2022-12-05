package org.randomcoder.website.jaxrs.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
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
import org.randomcoder.website.Config;
import org.randomcoder.website.bo.ArticleBusiness;
import org.randomcoder.website.bo.TagBusiness;
import org.randomcoder.website.command.ArticleAddCommand;
import org.randomcoder.website.command.ArticleEditCommand;
import org.randomcoder.website.command.CommentCommand;
import org.randomcoder.website.controller.ArticleController;
import org.randomcoder.website.controller.ArticleTagListController;
import org.randomcoder.website.controller.HomeController;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.ContentType;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.Tag;
import org.randomcoder.website.model.Roles;
import org.randomcoder.website.thymeleaf.ThymeleafEntity;
import org.randomcoder.website.validation.ArticleAddValidator;
import org.randomcoder.website.validation.ArticleEditValidator;
import org.randomcoder.website.validation.CommentValidator;
import org.randomcoder.website.validation.ValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
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
    ArticleAddValidator articleAddValidator;

    @Inject
    ArticleEditValidator articleEditValidator;

    @Inject
    UriInfo uriInfo;

    @Inject
    SecurityContext securityContext;

    @Inject
    HttpHeaders headers;

    @Inject
    HttpServletRequest request;

    @Inject
    @Named(Config.ARTICLE_PAGESIZE_MAX)
    int articlePageSizeMax = 100;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public ThymeleafEntity home() {
        return new ThymeleafEntity("home").withVariables(homeController.buildModel(uriInfo));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Page<Article> articlesApi(
            @QueryParam("offset") @DefaultValue("0") long offset,
            @QueryParam("length") @DefaultValue("10") long length) {

        if (length > articlePageSizeMax) {
            length = articlePageSizeMax;
            offset = 0;
        }

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
            @QueryParam("length") @DefaultValue("10") long length) {

        if (length > articlePageSizeMax) {
            length = articlePageSizeMax;
            offset = 0;
        }

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

    @GET
    @RolesAllowed({Roles.POST_ARTICLES, Roles.MANAGE_ARTICLES})
    @Path("/article/add")
    public ThymeleafEntity addArticle() {
        ArticleAddCommand command = new ArticleAddCommand();

        return new ThymeleafEntity("article-add")
                .withVariable("command", command)
                .withVariable("errors", new HashMap<>())
                .withVariable("contentTypes", ContentType.values());
    }

    @POST
    @RolesAllowed({Roles.POST_ARTICLES, Roles.MANAGE_ARTICLES})
    @Path("/article/add")
    public Response addArticleSubmit(@BeanParam ArticleAddCommand command, @FormParam("cancel") String cancel) {

        if (cancel != null) {
            return Response.status(Response.Status.FOUND).location(URI.create("/")).build();
        }

        var context = new ValidatorContext(headers.getLanguage());
        articleAddValidator.validate(context, command);
        var errors = context.getErrors();

        if (!errors.isEmpty()) {
            return Response.ok(new ThymeleafEntity("article-add")
                            .withVariable("command", command)
                            .withVariable("errors", errors)
                            .withVariable("contentTypes", ContentType.values()))
                    .build();
        }

        Article article = articleBusiness.createArticle(command, securityContext.getUserPrincipal().getName());

        return Response.status(Response.Status.FOUND)
                .location(URI.create(article.getPermalinkUrl()))
                .build();
    }

    @GET
    @RolesAllowed({Roles.POST_ARTICLES, Roles.MANAGE_ARTICLES})
    @Path("/article/edit")
    public ThymeleafEntity editArticle(@QueryParam("id") long id) {
        var command = new ArticleEditCommand();

        articleBusiness.loadArticleForEditing(command::load, id, securityContext.getUserPrincipal().getName());

        return new ThymeleafEntity("article-edit")
                .withVariable("command", command)
                .withVariable("errors", new HashMap<>())
                .withVariable("contentTypes", ContentType.values());
    }

    @POST
    @RolesAllowed({Roles.POST_ARTICLES, Roles.MANAGE_ARTICLES})
    @Path("/article/edit")
    public Response editArticleSubmit(
            @BeanParam ArticleEditCommand command, @QueryParam("id") long id, @FormParam("cancel") String cancel) {

        if (cancel != null) {
            return Response.status(Response.Status.FOUND).location(URI.create("/")).build();
        }

        var context = new ValidatorContext(headers.getLanguage());
        articleEditValidator.validate(context, command);
        var errors = context.getErrors();

        if (!errors.isEmpty()) {
            return Response.ok(new ThymeleafEntity("article-edit")
                            .withVariable("command", command)
                            .withVariable("errors", errors)
                            .withVariable("contentTypes", ContentType.values()))
                    .build();
        }

        var article = articleBusiness.updateArticle(command, command.getId(), securityContext.getUserPrincipal().getName());

        return Response.status(Response.Status.FOUND)
                .location(URI.create(article.getPermalinkUrl()))
                .build();
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

    @DELETE
    @Path("/article/id/{id}")
    @RolesAllowed({Roles.POST_ARTICLES, Roles.MANAGE_ARTICLES})
    public void deleteArticle(@PathParam("id") long id) {
        articleBusiness.deleteArticle(securityContext.getUserPrincipal().getName(), id);
    }

    @POST
    @Path("/article/id/{id}/delete")
    @RolesAllowed({Roles.POST_ARTICLES, Roles.MANAGE_ARTICLES})
    public Response deleteArticleBrowser(@PathParam("id") long id, @FormParam("_verb") String verb) {
        if (!"DELETE".equals(verb)) {
            throw new BadRequestException();
        }
        articleBusiness.deleteArticle(securityContext.getUserPrincipal().getName(), id);
        return Response
                .status(Response.Status.FOUND)
                .location(URI.create("/"))
                .build();
    }

}
