package org.randomcoder.cardspace;

import java.io.Serializable;
import java.util.Date;

import org.randomcoder.io.Producer;
import org.randomcoder.user.CardSpaceToken;

/**
 * JavaBean which holds the details necessary to identify a CardSpace token.
 * 
 * <pre>
 * Copyright (c) 2007, Craig Condit. All rights reserved.
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
public final class CardSpaceTokenSpec implements Serializable, Producer<CardSpaceToken>
{	
	private static final long serialVersionUID = -175193395317583318L;
	
	private String ppid;
	private String issuerHash;
	private Date expirationDate;
	
	/**
	 * Creates a new CardSpaceToken spec.
	 * @param ppid private personal identifier
	 * @param issuerHash issuer hash
	 * @param expirationDate expiration date
	 */
	public CardSpaceTokenSpec(String ppid, String issuerHash, Date expirationDate)
	{
		this.ppid = ppid;
		this.issuerHash = issuerHash;
		this.expirationDate = expirationDate;
	}
	
	/**
	 * Gets the private personal identifier for this token.
	 * @return ppid
	 */
	public String getPpid()
	{
		return ppid;
	}
	
	/**
	 * Gets the hash of the issuer for this token.
	 * @return issuer hash
	 */
	public String getIssuerHash()
	{
		return issuerHash;
	}
	
	/**
	 * Gets the expiration date of this token.
	 * @return expiration date
	 */
	public Date getExpirationDate()
	{
		return expirationDate;
	}

	@Override
	public void produce(CardSpaceToken target)
	{
		target.setPrivatePersonalIdentifier(ppid);
		target.setIssuerHash(issuerHash);
	}
}
