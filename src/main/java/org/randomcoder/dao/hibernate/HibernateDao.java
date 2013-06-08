package org.randomcoder.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.logging.*;
import org.hibernate.*;
import org.randomcoder.dao.CrudDao;
import org.randomcoder.dao.finder.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

/**
 * Hibernate implementation of CrudDao.
 * 
 * <p>
 * Inspired by Per Mellqvist's IBM developerWorks article, <a
 * href="http://www-128.ibm.com/developerworks/java/library/j-genericdao.html"
 * >Don't repeat the DAO!</a>.
 * </p>
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
 * 
 * @param <T>
 *            entity type
 * @param <PK>
 *            primary key type
 */
public class HibernateDao<T, PK extends Serializable> implements CrudDao<T, PK>, FinderExecutor
{
	private static final Log logger = LogFactory.getLog(HibernateDao.class);
	
	private SessionFactory sessionFactory;

	private FinderNamingStrategy namingStrategy = new DefaultFinderNamingStrategy();
	private boolean allowCreate = true;

	private Class<T> type;

	/**
	 * Sets the session factory implementation to use.
	 * @param sessionFactory session factory
	 */
	@Required
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Determines if sessions are allowed to be auto-created (defaults to true).
	 * 
	 * @param allowCreate
	 *            <code>true</code> to allow session creation
	 */
	public void setAllowCreate(boolean allowCreate)
	{
		this.allowCreate = allowCreate;
	}
	
	/**
	 * Creates a new Hibernate DAO for the specified type.
	 * 
	 * @param type
	 *            DAO type
	 */
	public HibernateDao(Class<T> type)
	{
		this.type = type;
	}

	@Override
	@SuppressWarnings("unchecked")
	public PK create(T o)
	{
		return (PK) getSession().save(o);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T read(PK id)
	{
		return (T) getSession().get(type, id);
	}

	@Override
	public void update(T o)
	{
		getSession().update(o);
	}

	@Override
	public void delete(T o)
	{
		getSession().delete(o);
	}

	@Override
	public int count(Method method, Object[] args)
	{
		Query query = prepareQuery(method, args, 0, 0);
		Number result = (Number) query.uniqueResult();
		if (result == null)
			return 0;
		return result.intValue();
	}

	@Override
	public Object find(Method method, Object[] args)
	{
		return prepareQuery(method, args, 0, 0).uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> list(Method method, final Object[] args)
	{
		return prepareQuery(method, args, 0, 0).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> list(Method method, final Object[] args, int start, int limit)
	{
		return prepareQuery(method, args, start, limit).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterator<T> iterate(Method method, final Object[] args)
	{
		return prepareQuery(method, args, 0, 0).iterate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterator<T> iterate(Method method, final Object[] args, int start, int limit)
	{
		return prepareQuery(method, args, start, limit).iterate();
	}

	/**
	 * Gets the current Hibernate session from the session factory.
	 * @return hibernate session
	 */
	protected Session getSession()
	{
		Session session = SessionFactoryUtils.getSession(sessionFactory, allowCreate);
		if (logger.isTraceEnabled())
		{
			logger.trace("Session: " + session.toString());
		}		
		return session;
	}

	private Query prepareQuery(Method method, Object[] args, int start, int limit)
	{
		String queryName = namingStrategy.queryNameFromMethod(type, method);
		Query query = getSession().getNamedQuery(queryName);
		setPositionalParams(query, args);

		if (start > 0) query.setFirstResult(start);
		if (limit > 0) query.setMaxResults(limit);

		return query;
	}

	private void setPositionalParams(Query query, Object[] args)
	{
		if (args == null || args.length == 0) return;
		for (int i = 0; i < args.length; i++) query.setParameter(i, args[i]);
	}
}
