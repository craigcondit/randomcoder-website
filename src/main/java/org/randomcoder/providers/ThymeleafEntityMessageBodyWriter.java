package org.randomcoder.providers;

import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import org.randomcoder.thymeleaf.ThymeleafContext;
import org.randomcoder.thymeleaf.ThymeleafEntity;
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

    private static final Logger LOG = LoggerFactory.getLogger(ThymeleafEntityMessageBodyWriter.class);

    @Inject
    public ITemplateEngine engine;

    @Inject
    public SecurityContext securityContext;

    public ThymeleafEntityMessageBodyWriter() {
        LOG.info("Initializing Thymeleaf entity message body writer");
    }

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

        String result = engine.process(entity.getView(), new ThymeleafContext(entity, securityContext));
        entityStream.write(result.getBytes(StandardCharsets.UTF_8));
    }

}