package com.randomcoder.saml;

import java.io.Serializable;
import java.util.Locale;

/**
 * SAML Attribute specification suitable for map keys.
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
public class SamlAttributeSpec implements Comparable<SamlAttributeSpec>, Serializable
{
	private static final long serialVersionUID = -536785258783107725L;
	
	private String namespace;
	private String local;
	
	/**
	 * Creates a new SamlAttributeSpec from the given namespace and local part.
	 * @param namespace namespace
	 * @param local local part
	 * @throws IllegalArgumentException if either parameter is null
	 */
	public SamlAttributeSpec(String namespace, String local)
	throws IllegalArgumentException
	{
		if (namespace == null) throw new IllegalArgumentException("namespace is required");
		if (local == null) throw new IllegalArgumentException("local is required");
		
		this.namespace = namespace;
		this.local = local.toLowerCase(Locale.US);
	}
	
	/**
	 * Gets the namespace of this attribute.
	 * @return namespace
	 */
	public String getNamespace()
	{
		return namespace;
	}
	
	/**
	 * Gets the localname of this attribute.
	 * @return namespace
	 */
	public String getLocal()
	{
		return local;
	}
	
	/**
	 * Determines if this <code>SamlAttributeSpec</code> is equal
	 * to another instance of this class.
	 * 
	 * <p>
	 * This method will return true if and only if the namespace and local
	 * fields in the two classes are equal.
	 * </p>
	 * @param obj object to compare
	 * @return true if equal, false otherwise
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof SamlAttributeSpec)) return false;
		if (obj == null) return false;
		
		SamlAttributeSpec other = (SamlAttributeSpec) obj;
		
		if (!namespace.equals(other.namespace)) return false;
		return local.equals(other.local);
	}

	/**
	 * Calculates a hash code.
	 * @return hash code
	 */
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	/**
	 * Compares this object with another instance of the same class.
	 * <p>
	 * This method compares by namespace and then by local name.
	 * @param other other object to compare
	 * @return 0 if equal, -1 if this object is first, 1 if this object is last
	 */
	public int compareTo(SamlAttributeSpec other)
	{
		int result = namespace.compareTo(other.namespace);
		if (result != 0) return result;
		return local.compareTo(other.local);
	}

	/**
	 * Generates a string representation of this class.
	 * <p>
	 * The format of the returned string is {namespace}:{local}.
	 * </p>
	 * @return string version 
	 */
	@Override
	public String toString()
	{
		return new StringBuilder(namespace).append(":").append(local).toString();
	}
	
}
