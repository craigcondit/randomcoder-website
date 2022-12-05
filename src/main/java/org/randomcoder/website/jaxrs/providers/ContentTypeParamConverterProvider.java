package org.randomcoder.website.jaxrs.providers;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import org.randomcoder.website.data.ContentType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class ContentTypeParamConverterProvider implements ParamConverterProvider {

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType != ContentType.class) {
            return null;
        }

        return (ParamConverter<T>) new ContentTypeParamConverter();
    }

    private static class ContentTypeParamConverter implements ParamConverter<ContentType> {

        @Override
        public ContentType fromString(String value) {
            if (value == null || value.isEmpty()) {
                return null;
            }
            return ContentType.valueOf(value);
        }

        @Override
        public String toString(ContentType value) {
            if (value == null) {
                return null;
            }
            return value.name();
        }

    }

}
