package org.randomcoder.mvc.resolvers;

import org.randomcoder.dao.Pagination;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PaginationArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pagination.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Pagination p = new Pagination();
        String size = webRequest.getParameter("page.size");
        if (size != null) {
            try {
               p.setSize(Long.valueOf(size));
            } catch (NumberFormatException ignored) {
            }
        }
        String page = webRequest.getParameter("page.page");
        if (page != null) {
            try {
                p.setPage(Long.valueOf(page));
            } catch (NumberFormatException ignored) {
            }
        }
        return p;
    }
}
