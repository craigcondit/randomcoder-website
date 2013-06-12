package org.randomcoder.download;

import java.util.List;

/**
 * Interface used to query a list of available packages.
 */
public interface PackageListProducer
{
	/**
	 * Gets a list of available packages.
	 * 
	 * @throws PackageListException
	 *           if an error occurs
	 * @return List of Package objects
	 */
	public List<Package> getPackages() throws PackageListException;
}
