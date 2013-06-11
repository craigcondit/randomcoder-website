package org.randomcoder.article;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.randomcoder.bo.ArticleBusiness;
import org.randomcoder.content.ContentType;
import org.randomcoder.db.TagDao;
import org.randomcoder.springmvc.EnumPropertyEditor;
import org.randomcoder.tag.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.CancellableFormController;

/**
 * Base class for article add / edit controllers.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public class AbstractArticleController extends CancellableFormController
{

	/**
	 * Article business object.
	 */
	protected ArticleBusiness articleBusiness;

	private TagDao tagDao;
	
	/**
	 * Sets the ArticleBusiness implementation to use.
	 * @param articleBusiness ArticleBusiness implementation
	 */
	@Required
	public void setArticleBusiness(ArticleBusiness articleBusiness)
	{
		this.articleBusiness = articleBusiness;
	}
	
	/**
	 * Sets the TagDao implementation to use.
	 * @param tagDao TagDao implementation
	 */
	@Required
	public void setTagDao(TagDao tagDao)
	{
		this.tagDao = tagDao;
	}
	
	/**
	 * Associates custom property editors with form objects.
	 */
	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception
	{
		super.initBinder(request, binder);
		binder.registerCustomEditor(ContentType.class, new EnumPropertyEditor(ContentType.class));
		binder.registerCustomEditor(TagList.class, new TagListPropertyEditor(tagDao));
	}

	/**
	 * Populates current model with required data for the form.
	 */
	@Override
	protected Map referenceData(HttpServletRequest request)
	{
		logger.debug("referenceData()");

		// populate parameters
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contentTypes", ContentType.values());
		return params;
	}

}
