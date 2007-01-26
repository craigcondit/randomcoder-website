package com.randomcoder.cardspace;

import java.util.Locale;

import org.apache.commons.codec.digest.DigestUtils;

import com.randomcoder.security.cardspace.CardSpaceCredentials;

public final class CardSpaceUtils
{
	private CardSpaceUtils() {}
	
	public static String calculateIssuerHash(CardSpaceCredentials credentials)
	{
		return DigestUtils.shaHex(credentials.getIssuerPublicKey()).toLowerCase(Locale.US);
	}
	
}
