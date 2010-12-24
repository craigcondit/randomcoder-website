package org.randomcoder.cardspace;

import java.util.Date;

import junit.framework.TestCase;

import org.randomcoder.user.CardSpaceToken;

public class CardSpaceTokenSpecTest extends TestCase
{
	private Date expirationDate;
	private String ppid;
	private String issuerHash;
	
	private CardSpaceTokenSpec spec;
	
	@Override
	protected void setUp() throws Exception
	{
		expirationDate = new Date();
		ppid = "PPID";
		issuerHash = "ISSUER_HASH";
		spec = new CardSpaceTokenSpec(ppid, issuerHash, expirationDate);
	}

	@Override
	protected void tearDown() throws Exception
	{
		spec = null;
		expirationDate = null;
		ppid = null;
		issuerHash = null;
	}

	public void testGetPpid()
	{
		assertEquals(ppid, spec.getPpid());
	}

	public void testGetIssuerHash()
	{
		assertEquals(issuerHash, spec.getIssuerHash());
	}

	public void testGetExpirationDate()
	{
		assertEquals(expirationDate, spec.getExpirationDate());
	}

	public void testProduce()
	{
		CardSpaceToken target = new CardSpaceToken();		
		spec.produce(target);
		
		assertEquals("Wrong ppid", ppid, target.getPrivatePersonalIdentifier());
		assertEquals("Wrong issuer hash", issuerHash, target.getIssuerHash());
	}
}
