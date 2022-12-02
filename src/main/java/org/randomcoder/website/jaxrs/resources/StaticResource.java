package org.randomcoder.website.jaxrs.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Objects;

@Path("")
@PermitAll
public class StaticResource {

    private static final Logger logger = LoggerFactory.getLogger(StaticResource.class);

    @GET
    @RolesAllowed("*")
    @Path("/redirect")
    public Response redirect(@QueryParam("url") String uri) {
        return Response.status(Response.Status.FOUND)
                .location(URI.create(uri))
                .build();
    }

    @GET
    @Path("/favicon.ico")
    public Response favicon() {
        return staticResource("favicon.ico");
    }

    @GET
    @Path("/robots.txt")
    public Response robots() {
        return staticResource("robots.txt");
    }

    @GET
    @Path("/css/{path: .+}")
    public Response css(@PathParam("path") String path) {
        return staticResource(String.format("css/%s", path));
    }

    @GET
    @Path("/images/{path: .+}")
    public Response images(@PathParam("path") String path) {
        return staticResource(String.format("images/%s", path));
    }

    @GET
    @Path("/js/{path: .+}")
    public Response js(@PathParam("path") String path) {
        return staticResource(String.format("js/%s", path));
    }

    Response staticResource(String path) {
        // make sure path is well-formed
        if (path.contains("..")) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var resource = getClass().getResourceAsStream(String.format("/org/randomcoder/website/content/%s", path));
        return Objects.isNull(resource)
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(resource, mediaType(path)).build();
    }

    MediaType mediaType(String path) {
        String suffix = path.replaceAll(".*\\.", "");
        return switch (suffix) {
            case "txt" -> MediaType.TEXT_PLAIN_TYPE;
            case "js" -> MediaType.valueOf("text/javascript");
            case "css" -> MediaType.valueOf("text/css");
            case "png" -> MediaType.valueOf("image/png");
            case "jpg", "jpeg" -> MediaType.valueOf("image/jpeg");
            case "gif" -> MediaType.valueOf("image/gif");
            case "ico" -> MediaType.valueOf("image/x-icon");
            default -> MediaType.APPLICATION_OCTET_STREAM_TYPE;
        };
    }

}