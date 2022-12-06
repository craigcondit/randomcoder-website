package org.randomcoder.website.jaxrs.resources;

import com.codahale.metrics.MetricRegistry;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("")
public class MetricsResource {

    @Inject
    MetricRegistry registry;

    @GET
    @Path("metrics")
    @Produces({MediaType.APPLICATION_JSON, "text/csv"})
    public Response metrics() {

        var cacheControl = new CacheControl();
        cacheControl.setMustRevalidate(true);
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);

        return Response.ok(registry)
                .cacheControl(cacheControl)
                .build();
    }

}
