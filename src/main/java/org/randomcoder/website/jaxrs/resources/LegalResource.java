package org.randomcoder.website.jaxrs.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.randomcoder.website.thymeleaf.ThymeleafEntity;

@Path("/legal")
@PermitAll
public class LegalResource {

    @GET
    @Path("about")
    @Produces(MediaType.TEXT_HTML)
    public ThymeleafEntity about() {
        return new ThymeleafEntity("legal-about");
    }

    @GET
    @Path("license")
    @Produces(MediaType.TEXT_HTML)
    public ThymeleafEntity license() {
        return new ThymeleafEntity("legal-license");
    }

}
