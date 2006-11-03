package com.randomcoder.article;

import java.util.Date;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.randomcoder.io.*;
import com.randomcoder.security.UnauthorizedException;
import com.randomcoder.tag.*;
import com.randomcoder.user.*;

/**
 * Business implementation which handles articles.
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
public class ArticleBusinessImpl implements ArticleBusiness
{
	private static final String ROLE_ARTICLE_ADMIN = "article-admin";
	
	private UserDao userDao;
	private RoleDao roleDao;
	private ArticleDao articleDao;
	private TagDao tagDao;
	private CommentDao commentDao;
		
	@Required
	public void setUserDao(UserDao userDao) { this.userDao = userDao; }
	
	@Required
	public void setRoleDao(RoleDao roleDao) { this.roleDao = roleDao; }
	
	@Required
	public void setArticleDao(ArticleDao articleDao) { this.articleDao = articleDao; }
	
	@Required
	public void setTagDao(TagDao tagDao) { this.tagDao = tagDao; }

	@Required
	public void setCommentDao(CommentDao commentDao) { this.commentDao = commentDao; }
	
	@Transactional
	public void createArticle(Producer<Article> producer, String userName)
	{
		User user = findUserByName(userName);

		Article article = new Article();

		producer.produce(article);
		
		article.setCreatedByUser(user);
		article.setCreationDate(new Date());
		
		// save tags
		for (Tag tag : article.getTags())
		{
			if (tag.getId() == null) tagDao.create(tag);
		}

		articleDao.create(article);		
	}

	@Transactional
	public void createComment(Producer<Comment> producer, Long articleId, String userName)
	{
		User user = userName == null ? null : findUserByName(userName);

		Article article = loadArticle(articleId);
		
		Comment comment = new Comment();
		
		producer.produce(comment);
		
		comment.setCreatedByUser(user);
		comment.setCreationDate(new Date());
		comment.setArticle(article);
		
		commentDao.create(comment);
		
		article.getComments().add(comment);
	}

	@Transactional
	public void updateArticle(Producer<Article> producer, Long articleId, String userName)
	{
		User user = findUserByName(userName);

		Article article = loadArticle(articleId);

		checkAuthorUpdate(user, article);

		producer.produce(article);
		
		article.setModifiedByUser(user);
		article.setModificationDate(new Date());

		// save tags
		for (Tag tag : article.getTags())
		{
			if (tag.getId() == null) tagDao.create(tag);
		}
		
		articleDao.update(article);		
	}

	@Transactional(readOnly=true)
	public void loadArticleForEditing(Consumer<Article> consumer, Long articleId, String userName)
	{
		User user = findUserByName(userName);		
		Article article = loadArticle(articleId);

		checkAuthorUpdate(user, article);
		
		consumer.consume(article);
	}
	
	@Transactional
	public void deleteArticle(String userName, Long articleId)
	{
		User user = findUserByName(userName);
		Article article = loadArticle(articleId);
		
		checkAuthorDelete(user, article);
		
		articleDao.delete(article);
	}
	
	private void checkAuthorUpdate(User user, Article article)
	{
		checkAuthor(user, article, "You are not allowed to edit articles you did not create.");
	}

	private void checkAuthorDelete(User user, Article article)
	{
		checkAuthor(user, article, "You are not allowed to delete articles you did not create.");
	}

	private void checkAuthor(User user, Article article, String errorMessage)
	{
		Role articleAdmin = findRoleByName(ROLE_ARTICLE_ADMIN);
		
		if (!user.getRoles().contains(articleAdmin))
		{
			// make sure user created this article
			User createdBy = article.getCreatedByUser();

			if (createdBy == null || !user.getId().equals(createdBy.getId()))
				throw new UnauthorizedException(errorMessage);
		}
	}
	
	private User findUserByName(String userName) throws UserNotFoundException
	{
		User user = userDao.findByUserName(userName);
		
		if (user == null)
			throw new UserNotFoundException("Unknown user: " + userName);
		return user;
	}
	
	private Article loadArticle(Long articleId) throws ArticleNotFoundException
	{
		if (articleId == null)
			throw new ArticleNotFoundException("Invalid id specified.");
		
		Article article = articleDao.read(articleId);

		if (article == null)
			throw new ArticleNotFoundException("No article exists with id: " + articleId);
		return article;
	}
	
	private Role findRoleByName(String roleName) throws RoleNotFoundException
	{
		Role role = roleDao.findByName(roleName);
		
		if (role == null)
			throw new RoleNotFoundException("Unknown role: " + roleName);
		
		return role;
	}
}
