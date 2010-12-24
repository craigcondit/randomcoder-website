package org.randomcoder.security.cardspace;

import java.util.Date;

import org.hibernate.Query;

import org.randomcoder.dao.hibernate.HibernateDao;

/**
 * CardSpaceSeenToken data access implementation.
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
public class CardSpaceSeenTokenDaoImpl
extends HibernateDao<CardSpaceSeenToken, Long>
implements CardSpaceSeenTokenDaoBase
{
	private static final String QUERY_DELETE_BEFORE = "CardSpaceSeenToken.DeleteBefore";
	/**
	 * Default constructor.
	 */
	public CardSpaceSeenTokenDaoImpl()
	{
		super(CardSpaceSeenToken.class);
	}

	/**
	 * Deletes all tokens created before the cutoff date.
	 * @param cutoff cutoff date
	 */
	@Override
	public void deleteBefore(Date cutoff)
	{
		Query query = getSession().getNamedQuery(QUERY_DELETE_BEFORE);
		query.setParameter(0, cutoff);
		query.executeUpdate();
	}

}
