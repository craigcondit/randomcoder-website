package org.randomcoder.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.randomcoder.thymeleaf.ThymeleafEntity;

@Path("/legal")
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
