package org.randomcoder.test.mock.dao;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import org.randomcoder.security.cardspace.*;

@SuppressWarnings("javadoc")
public class CardSpaceSeenTokenDaoMock implements CardSpaceSeenTokenDao
{
	private final List<CardSpaceSeenToken> tokens = new ArrayList<CardSpaceSeenToken>();
	private long primaryKey = 0;

	@Override
	public CardSpaceSeenToken findByKey(String assertionId, String privatePersonalIdentifier, String issuerHash)
	{
		for (CardSpaceSeenToken token : tokens)
		{
			if (token.getAssertionId().equals(assertionId) &&
					token.getPrivatePersonalIdentifier().equals(privatePersonalIdentifier) &&
					token.getIssuerHash().equals(issuerHash))
				return token;
		}
		return null;
	}

	@Override
	public void deleteBefore(Date cutoff)
	{
		for (Iterator<CardSpaceSeenToken> it = tokens.iterator(); it.hasNext();)
		{
			CardSpaceSeenToken token = it.next();
			if (token.getCreationDate().before(cutoff)) it.remove();
		}

	}

	@Override
	public Long create(CardSpaceSeenToken newInstance)
	{
		validateRequiredFields(newInstance);
		
		CardSpaceSeenToken current = findByKey(newInstance.getAssertionId(), newInstance.getPrivatePersonalIdentifier(), newInstance.getIssuerHash());
		if (current != null) throw new IllegalArgumentException("UNIQUE violation: assertionId = " + newInstance.getAssertionId());
				
		newInstance.setId(primaryKey++);
		tokens.add(newInstance);
		
		return newInstance.getId();
	}

	@Override
	public CardSpaceSeenToken read(Long id)
	{
		for (Iterator<CardSpaceSeenToken> it = tokens.iterator(); it.hasNext();)
		{
			CardSpaceSeenToken token = it.next();
			if (token.getId().equals(id)) return token;
		}
		return null;
	}

	private void validateRequiredFields(CardSpaceSeenToken newInstance)
	{
		if (StringUtils.trimToNull(newInstance.getAssertionId()) == null)
			throw new IllegalArgumentException("AssertionID is required");
		if (StringUtils.trimToNull(newInstance.getPrivatePersonalIdentifier()) == null)
			throw new IllegalArgumentException("PPID is required");
		if (StringUtils.trimToNull(newInstance.getIssuerHash()) == null)
			throw new IllegalArgumentException("IssuerHash is required");
		if (newInstance.getCreationDate() == null)
			throw new IllegalArgumentException("Creation date is required");
	}
}
