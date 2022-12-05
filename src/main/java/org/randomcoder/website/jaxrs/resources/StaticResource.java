package org.randomcoder.website.jaxrs.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import org.randomcoder.website.bo.CachedResource;
import org.randomcoder.website.bo.ResourceCache;

import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Path("")
@PermitAll
public class StaticResource {

    @Inject
    ResourceCache resourceCache;

    @Inject
    Request request;

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

        String qualifiedPath = String.format("/org/randomcoder/website/content/%s", path);

        var resource = resourceCache.loadResource(qualifiedPath, () -> loadResource(qualifiedPath));
        if (resource == null) {
            throw new NotFoundException();
        }

        var cc = new CacheControl();
        cc.setPrivate(true);
        cc.setMaxAge(3600);

        var builder = request.evaluatePreconditions(resource.lastModified(), resource.tag());
        if (builder != null) {
            return builder
                    .type(mediaType(path))
                    .cacheControl(cc)
                    .build();
        }

        return Response
                .ok(resource.content(), mediaType(path))
                .cacheControl(cc)
                .tag(resource.tag())
                .lastModified(resource.lastModified())
                .build();
    }

    byte[] loadResource(String path) {
        try (var resource = getClass().getResourceAsStream(path)) {
            if (resource == null) {
                return null;
            }
            return resource.readAllBytes();
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
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
