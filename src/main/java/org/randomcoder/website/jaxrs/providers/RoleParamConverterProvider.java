package org.randomcoder.website.jaxrs.providers;

import jakarta.inject.Inject;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import org.randomcoder.website.bo.UserBusiness;
import org.randomcoder.website.data.Role;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class RoleParamConverterProvider implements ParamConverterProvider {

    @Inject
    UserBusiness userBusiness;

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType != Role.class) {
            return null;
        }

        return (ParamConverter<T>) new RoleParamConverter(userBusiness);
    }

    private static class RoleParamConverter implements ParamConverter<Role> {

        private final UserBusiness userBusiness;

        private RoleParamConverter(UserBusiness userBusiness) {
            this.userBusiness = userBusiness;
        }

        @Override
        public Role fromString(String value) {
            return userBusiness.findRoleByName(value);
        }

        @Override
        public String toString(Role value) {
            if (value == null) {
                return null;
            }

            return value.getName();
        }

    }

}
