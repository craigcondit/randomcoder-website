package org.randomcoder.test.mock.dao;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import org.randomcoder.user.*;

public class CardSpaceTokenDaoMock implements CardSpaceTokenDao
{
	private final List<CardSpaceToken> tokens = new ArrayList<CardSpaceToken>();
	private long primaryKey = 0;

	@Override
	public CardSpaceToken findByPrivatePersonalIdentifier(String ppid, String issuerHash)
	{
		for (CardSpaceToken token : tokens)
		{
			if (token.getPrivatePersonalIdentifier().equals(ppid) && token.getIssuerHash().equals(issuerHash))
				return token;
		}
		return null;
	}

	@Override
	public List<CardSpaceToken> listByUser(User user)
	{
		List<CardSpaceToken> list = new ArrayList<CardSpaceToken>(tokens);
		Collections.sort(list, new CardSpaceTokenLoginDateComparator());
		return list;
	}

	@Override
	public void update(CardSpaceToken transientObject)
	{
		Long id = transientObject.getId();
		
		CardSpaceToken loaded = read(id);
		if (loaded == null)
			throw new IllegalArgumentException("NOT FOUND: id = " + id);
		
		validateRequiredFields(transientObject);
		
		delete(loaded);
		
		tokens.add(transientObject);
	}

	@Override
	public Long create(CardSpaceToken newInstance)
	{		
		validateRequiredFields(newInstance);
		
		for (CardSpaceToken token : tokens)
		{
			if (token.getPrivatePersonalIdentifier().equals(newInstance.getPrivatePersonalIdentifier()))
				throw new IllegalArgumentException("UNIQUE violation: ppid = " + newInstance.getPrivatePersonalIdentifier());
		}
				
		newInstance.setId(primaryKey++);
		tokens.add(newInstance);
		
		return newInstance.getId();
	}

	@Override
	public void delete(CardSpaceToken persistentObject)
	{
		Long id = persistentObject.getId();
		
		for (Iterator<CardSpaceToken> it = tokens.iterator(); it.hasNext();)
		{
			if (it.next().getId().equals(id))
			{
				it.remove();
				return;
			}
		}
		throw new IllegalArgumentException("NOT FOUND: id = " + id);
	}

	@Override
	public CardSpaceToken read(Long id)
	{
		for (Iterator<CardSpaceToken> it = tokens.iterator(); it.hasNext();)
		{
			CardSpaceToken token = it.next();
			if (token.getId().equals(id)) return token;
		}
		return null;
	}
	
	private static class CardSpaceTokenLoginDateComparator implements Comparator<CardSpaceToken>
	{
		public CardSpaceTokenLoginDateComparator() {}
		
		@Override
		public int compare(CardSpaceToken o1, CardSpaceToken o2)
		{
			Date d1 = o1.getLastLoginDate();
			Date d2 = o2.getLastLoginDate();
			
			return d1.compareTo(d2);
		}
		
	}

	private void validateRequiredFields(CardSpaceToken token)
	{
		if (StringUtils.trimToNull(token.getPrivatePersonalIdentifier()) == null)
			throw new IllegalArgumentException("ppid is required");
		
		if (StringUtils.trimToNull(token.getIssuerHash()) == null)
			throw new IllegalArgumentException("issuer hash is requird");
		
		if (StringUtils.trimToNull(token.getEmailAddress()) == null)
			throw new IllegalArgumentException("email address is required");
		
		if (token.getCreationDate() == null)
			throw new IllegalArgumentException("creation date is required");
	}

}
