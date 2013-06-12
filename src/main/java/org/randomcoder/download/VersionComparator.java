package org.randomcoder.download;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

/**
 * Comparator which compares project version numbers.
 */
public class VersionComparator implements Comparator<String>
{
	/**
	 * Compares one artifact version to another.
	 * <p>
	 * NOTE: This comparison is somewhat naive... it will split version components
	 * by periods, and then by dashes, comparing each part numerically if
	 * possible.
	 * </p>
	 * 
	 * @param version1
	 *          first version
	 * @param version2
	 *          second version
	 * @return a negative integer, zero, or a positive integer as the first
	 *         argument is less than, equal to, or greater than the second.
	 */
	@Override
	public int compare(String version1, String version2)
	{
		// sanitize
		version1 = StringUtils.trimToEmpty(version1);
		version2 = StringUtils.trimToEmpty(version2);

		// split into version and qualifier
		String[] parts1 = version1.split("-", 2);
		String[] parts2 = version2.split("-", 2);

		String ver1 = (parts1.length > 0) ? parts1[0] : "";
		String ver2 = (parts2.length > 0) ? parts2[0] : "";

		String[] rev1 = ver1.split("\\.");
		String[] rev2 = ver2.split("\\.");

		for (int i = 0; i < Math.max(rev1.length, rev2.length); i++)
		{
			String r1 = rev1.length > i ? rev1[i] : "";
			String r2 = rev2.length > i ? rev2[i] : "";
			if (r1.length() == 0 && r2.length() == 0)
				continue; // blank

			int result = toNumber(r1).compareTo(toNumber(r2));
			if (result != 0)
				return result;
		}

		// versions are equal, parse out qualifiers
		String[] qual1 = (parts1.length == 2) ? parts1[1].split("-") : new String[] {};
		String[] qual2 = (parts2.length == 2) ? parts2[1].split("-") : new String[] {};

		for (int i = 0; i < Math.max(qual1.length, qual2.length); i++)
		{
			String q1 = qual1.length > i ? qual1[i] : "";
			String q2 = qual2.length > i ? qual2[i] : "";

			if (isNumeric(q1) && isNumeric(q2))
			{
				int result = toNumber(q1).compareTo(toNumber(q2));
				if (result != 0)
					return result;
			}
			else
			{
				int result = q1.compareTo(q2);
				if (result != 0)
					return result;
			}
		}

		// everything is equal
		return 0;
	}

	private Integer toNumber(String value)
	{
		if (value == null || value.length() == 0)
			return 0;
		if (value.matches("[0-9]*"))
			return Integer.parseInt(value);
		return 0;
	}

	private boolean isNumeric(String value)
	{
		if (value == null || value.length() == 0)
			return false;
		return value.matches("[0-9]*");
	}
}