package com.randomcoder.bean;

import java.io.Serializable;

import javax.persistence.*;

import org.apache.commons.lang.builder.*;

/**
 * JavaBean representing a security role.
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
@NamedQueries
({
	@NamedQuery(name = "Role.All", query = "from Role r order by r.name"),
	@NamedQuery(name = "Role.ByName", query = "from Role r where r.name = ?", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") })
})
@Entity
@org.hibernate.annotations.Entity(mutable = false)
@Table(name = "roles")
@SequenceGenerator(name = "roles", sequenceName = "roles_seq", allocationSize = 1)
public class Role implements Serializable
{
	private static final long serialVersionUID = -828314946477973093L;

	private Long id;
	private String name;
	private String description;

	/**
	 * Gets the id of this role.
	 * @return role id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles")
	@Column(name = "role_id")
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the id of this role.
	 * @param id role id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the name of this role.
	 * @return role name
	 */
	@Column(name = "name", unique = true, nullable = false, length = 30)
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this role.
	 * @param name role name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the description of this role.
	 * @return role description, or null if not supplied.
	 */
	@Column(name = "description", nullable = true, length = 255)
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of this role.
	 * @param description role description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets a string representation of this object, suitable for debugging.
	 * @return string representation of this object
	 */
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
