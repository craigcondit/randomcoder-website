package org.randomcoder.crypto;

import java.io.Serializable;

@SuppressWarnings("javadoc")
public class EncryptedObject implements Serializable
{
	private static final long serialVersionUID = -6632756368780131317L;
	
	private String value;
	
	public EncryptedObject() {}
	public EncryptedObject(String value) { this.value = value; }
	public String getValue() { return value; }
	public void setValue(String value) { this.value = value; }
}
