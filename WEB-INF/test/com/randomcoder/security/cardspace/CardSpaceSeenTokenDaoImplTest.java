package com.randomcoder.security.cardspace;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.*;

import com.randomcoder.test.AbstractDaoTestCase;

public class CardSpaceSeenTokenDaoImplTest extends AbstractDaoTestCase
{
	private CardSpaceSeenTokenDao cardSpaceSeenTokenDao;
	
	@Before
	public void setUp() throws Exception
	{
		cleanDatabase();		
		cardSpaceSeenTokenDao = (CardSpaceSeenTokenDao)
			createDao(new CardSpaceSeenTokenDaoImpl(), CardSpaceSeenTokenDao.class);		
		bindSession();
	}

	@After
	public void tearDown() throws Exception
	{
		unbindSession();
		cardSpaceSeenTokenDao = null;
	}
	
	@Test
	public void testCreateRead() throws Exception
	{
		begin();
		CardSpaceSeenToken token = createToken("assertion1", "ppid1", "issuerHash1", new Date());
		commit();
		
		rebindSession();
		
		token = cardSpaceSeenTokenDao.read(token.getId());
		assertNotNull(token);
		assertEquals("assertion1", token.getAssertionId());		
		assertEquals("ppid1", token.getPrivatePersonalIdentifier());		
		assertEquals("issuerHash1", token.getIssuerHash());
		assertNotNull(token.toString());
	}

	@Test
	public void testDeleteBefore() throws Exception
	{
		Date now = new Date();
		
		Date date1 = new Date(now.getTime()); // now
		Date date2 = new Date(now.getTime() - 3600000); // -1 hour
		Date date3 = new Date(now.getTime() - 7200000); // -2 hours
		
		Date cutoff = new Date(now.getTime() - 1800000); // 0.5 hours
			
		begin();
		createToken("assertion1", "ppid1", "issuerHash1", date1);
		createToken("assertion2", "ppid2", "issuerHash2", date2);
		createToken("assertion3", "ppid3", "issuerHash3", date3);
		commit();
		
		rebindSession();

		assertNotNull("Missing 1", cardSpaceSeenTokenDao.findByKey("assertion1", "ppid1", "issuerHash1"));
		assertNotNull("Missing 2", cardSpaceSeenTokenDao.findByKey("assertion2", "ppid2", "issuerHash2"));
		assertNotNull("Missing 3", cardSpaceSeenTokenDao.findByKey("assertion3", "ppid3", "issuerHash3"));
		
		begin();
		cardSpaceSeenTokenDao.deleteBefore(cutoff);
		commit();
		
		rebindSession();
		
		assertNotNull("Missing 1", cardSpaceSeenTokenDao.findByKey("assertion1", "ppid1", "issuerHash1"));
		assertNull("Exists 2", cardSpaceSeenTokenDao.findByKey("assertion2", "ppid2", "issuerHash2"));
		assertNull("Exists 3", cardSpaceSeenTokenDao.findByKey("assertion3", "ppid3", "issuerHash3"));
	}
	
	private CardSpaceSeenToken createToken(String assertionId, String ppid, String issuerHash, Date creationDate)
	{
		CardSpaceSeenToken token = new CardSpaceSeenToken();
		token.setAssertionId(assertionId);
		token.setPrivatePersonalIdentifier(ppid);
		token.setIssuerHash(issuerHash);
		token.setCreationDate(creationDate);
		cardSpaceSeenTokenDao.create(token);
		return token;
	}
}
