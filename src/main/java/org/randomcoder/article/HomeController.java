package org.randomcoder.article;

import java.util.*;

import org.randomcoder.db.Article;


/**
 * Controller class which handles the front page of the site.
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
public class HomeController extends AbstractArticleListController<ArticlePageCommand>
{
	
	@Override
	protected List<Article> listArticlesBetweenDates(ArticlePageCommand command, Date startDate, Date endDate)
	{
		return articleBusiness.listArticlesBetweenDates(startDate, endDate);
	}

	@Override
	protected List<Article> listArticlesBeforeDateInRange(ArticlePageCommand command, Date cutoffDate, int start, int limit)	
	{
		return articleBusiness.listArticlesBeforeDateInRange(cutoffDate, start, limit);
	}

	@Override
	protected int countArticlesBeforeDate(ArticlePageCommand command, Date cutoffDate)
	{
		return articleBusiness.countArticlesBeforeDate(cutoffDate);
	}

	@Override
	protected String getSubTitle(ArticlePageCommand command)
	{
		return null;
	}
}
