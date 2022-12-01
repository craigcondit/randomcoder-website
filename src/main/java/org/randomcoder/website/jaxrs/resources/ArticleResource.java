package org.randomcoder.website.jaxrs.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import org.randomcoder.website.bo.ArticleBusiness;
import org.randomcoder.website.bo.TagBusiness;
import org.randomcoder.website.controller.ArticleTagListController;
import org.randomcoder.website.controller.HomeController;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.Tag;
import org.randomcoder.website.thymeleaf.ThymeleafEntity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Path("")
@PermitAll
public class ArticleResource {

    @Inject
    HomeController homeController;

    @Inject
    ArticleTagListController articleTagListController;

    @Inject
    TagBusiness tagBusiness;

    @Inject
    ArticleBusiness articleBusiness;

    @Inject
    UriInfo uriInfo;

    @Inject
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public ThymeleafEntity home() {
        return new ThymeleafEntity("home")
                .withVariables(homeController.buildModel(uriInfo));
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
        return new ThymeleafEntity("article-tag-list")
                .withVariables(articleTagListController.buildModel(uriInfo));
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

}
