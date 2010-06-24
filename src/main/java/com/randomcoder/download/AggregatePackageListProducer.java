package com.randomcoder.download;

import java.util.*;

import org.springframework.beans.factory.annotation.Required;

/**
 * Package list producer which aggregates or combines other package lists. 
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
public class AggregatePackageListProducer implements PackageListProducer
{
	private List<PackageListProducer> producers;
	
	/**
	 * Sets the list of producers to query.
	 * @param producers list of PackageListProducer objects
	 */
	@Required
	public void setProducers(List<PackageListProducer> producers)
	{
		this.producers = producers;
	}

	/**
	 * Merges package lists from several different sources.
	 * <p>
	 * This method will combine packages with the same name while attempting to
	 * maintain the original package order by appending filesets onto existing
	 * packages.
	 * </p>
	 * @throws PackageListException if an error occurs
	 * @return List of Package elements
	 */
	@Override
	public List<Package> getPackages()
	throws PackageListException
	{
		List<Package> packageList = new ArrayList<Package>();
		Map<String, Package> packageMap = new HashMap<String, Package>();
		
		for (PackageListProducer producer : producers)
		{
			List<Package> packages = producer.getPackages();
			for (Package pkg : packages)
			{
				// find current package
				String pkgName = pkg.getName();
				Package current = packageMap.get(pkgName);
				if (current == null)
				{
					// add
					current = new Package();
					current.setName(pkg.getName());
					current.setDescription(pkg.getDescription());
					packageList.add(current);
					packageMap.put(pkgName, current);
				}
				
				// walk filesets and add to current package
				for (FileSet fs : pkg.getFileSets())
				{
					try
					{
						current.getFileSets().add((FileSet) fs.clone());
					}
					catch (CloneNotSupportedException e)
					{
						throw new PackageListException("FileSpec not cloneable", e);
					}
				}				
			}
		}
		
		// sort packages by name
		Collections.sort(packageList);
		
		return packageList;
	}
}
