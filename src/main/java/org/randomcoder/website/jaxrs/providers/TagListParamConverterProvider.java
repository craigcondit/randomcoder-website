package org.randomcoder.website.jaxrs.providers;

import jakarta.inject.Inject;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import org.apache.commons.lang3.StringUtils;
import org.randomcoder.website.bo.TagBusiness;
import org.randomcoder.website.data.Tag;
import org.randomcoder.website.model.TagList;
import org.randomcoder.website.validation.DataValidationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Provider
public class TagListParamConverterProvider implements ParamConverterProvider {

    @Inject
    TagBusiness tagBusiness;

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType != TagList.class) {
            return null;
        }

        return (ParamConverter<T>) new TagListParamConverter(tagBusiness);
    }

    private static class TagListParamConverter implements ParamConverter<TagList> {

        private final TagBusiness tagBusiness;

        private TagListParamConverter(TagBusiness tagBusiness) {
            this.tagBusiness = tagBusiness;
        }

        @Override
        public TagList fromString(String value) {
            value = StringUtils.trimToEmpty(value);

            String[] tagNames = value.split(",");

            Set<String> names = new HashSet<>();
            List<Tag> tags = new ArrayList<>();

            for (String tagName : tagNames) {
                tagName = tagName.replaceAll("\\s+", " ").trim();

                String name = DataValidationUtils.canonicalizeTagName(tagName);

                if (name != null && !names.contains(name)) {
                    // find tag in db
                    Tag tag = tagBusiness.findTagByName(name);
                    if (tag == null) {
                        // create a new one
                        tag = new Tag();
                        tag.setName(name);
                        tag.setDisplayName(tagName);
                    }

                    tags.add(tag);
                    names.add(name);
                }
            }

            return new TagList(tags);
        }

        @Override
        public String toString(TagList value) {
            if (value == null) {
                return null;
            }

            return value.getTags().stream()
                    .map(t -> t.getName())
                    .collect(Collectors.joining(","));
        }

    }

}
