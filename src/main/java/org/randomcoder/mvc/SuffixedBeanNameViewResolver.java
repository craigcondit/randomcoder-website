package org.randomcoder.mvc;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.BeanNameViewResolver;

public class SuffixedBeanNameViewResolver extends BeanNameViewResolver {
	private String suffix;

	public SuffixedBeanNameViewResolver(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public View resolveViewName(String viewName, Locale locale) throws BeansException {
		return super.resolveViewName(viewName + suffix, locale);
	}

}
