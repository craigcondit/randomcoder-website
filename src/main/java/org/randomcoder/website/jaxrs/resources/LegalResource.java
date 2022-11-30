package org.randomcoder.website.jaxrs.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.randomcoder.website.thymeleaf.ThymeleafEntity;

@Path("/legal")
@PermitAll
public class LegalResource {

    @GET
    @Path("about")
    public ThymeleafEntity about() {
        return new ThymeleafEntity("legal-about");
    }

    @GET
    @Path("license")
    public ThymeleafEntity license() {
        return new ThymeleafEntity("legal-license");
    }

}
