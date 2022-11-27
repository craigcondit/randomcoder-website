package org.randomcoder.website.jaxrs.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.randomcoder.website.controller.ArticleTagListController;
import org.randomcoder.website.controller.HomeController;
import org.randomcoder.website.thymeleaf.ThymeleafEntity;

@Path("")
public class ArticleResource {

    @Inject
    HomeController homeController;

    @Inject
    ArticleTagListController articleTagListController;

    @Inject
    UriInfo uriInfo;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public ThymeleafEntity home() {
        return new ThymeleafEntity("home")
                .withVariables(homeController.buildModel(uriInfo));
    }

    @GET
    @Path("/tags/{tagName}")
    @Produces(MediaType.TEXT_HTML)
    public ThymeleafEntity tag() {
        return new ThymeleafEntity("article-tag-list")
                .withVariables(articleTagListController.buildModel(uriInfo));
    }

}
