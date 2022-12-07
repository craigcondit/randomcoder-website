package org.randomcoder.website.jaxrs.providers;

import com.codahale.metrics.MetricRegistry;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import org.randomcoder.website.thymeleaf.ThymeleafContext;
import org.randomcoder.website.thymeleaf.ThymeleafEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Provider
@Produces(MediaType.TEXT_HTML)
public class ThymeleafEntityMessageBodyWriter implements MessageBodyWriter<ThymeleafEntity> {

    @Inject
    public ITemplateEngine engine;

    @Inject
    public SecurityContext securityContext;

    @Inject
    MetricRegistry metrics;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == ThymeleafEntity.class;
    }

    @Override
    public void writeTo(
            ThymeleafEntity entity,
            Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {

        String result;
        try (var unused = metrics.timer("thymeleaf.render." + entity.getView()).time()) {
            result = engine.process(entity.getView(), new ThymeleafContext(entity, securityContext));
        }
        entityStream.write(result.getBytes(StandardCharsets.UTF_8));
    }

}
