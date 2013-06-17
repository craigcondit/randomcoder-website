package org.randomcoder.download;

import java.util.*;

import org.springframework.beans.factory.annotation.Required;

/**
 * Package list producer which aggregates or combines other package lists.
 */
public class AggregatePackageListProducer implements PackageListProducer
{
	private List<PackageListProducer> producers;

	/**
	 * Sets the list of producers to query.
	 * 
	 * @param producers
	 *          list of PackageListProducer objects
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
	 * 
	 * @throws PackageListException
	 *           if an error occurs
	 * @return List of Package elements
	 */
	@Override
	public List<Package> getPackages() throws PackageListException
	{
		List<Package> packageList = new ArrayList<>();
		Map<String, Package> packageMap = new HashMap<>();

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