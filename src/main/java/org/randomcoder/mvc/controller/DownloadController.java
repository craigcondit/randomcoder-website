package org.randomcoder.mvc.controller;

import java.util.*;

import javax.inject.*;

import org.apache.commons.lang.StringUtils;
import org.randomcoder.download.*;
import org.randomcoder.download.Package;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller which generates download links.
 */
@Controller("downloadController")
public class DownloadController
{
	private int maximumVersionCount = 1;
	private PackageListProducer packageListProducer;

	/**
	 * Sets the maximum number of versions to display per project.
	 * 
	 * @param maximumVersionCount
	 *          maximum number of versions
	 */
	@Value("${download.max.versions.per.package}")
	public void setMaximumVersionCount(int maximumVersionCount)
	{
		this.maximumVersionCount = maximumVersionCount;
	}

	/**
	 * Sets the PackageListProducer implementation to use.
	 * 
	 * @param packageListProducer
	 *          package list producer
	 */
	@Inject
	@Named("packageListProducer")
	public void setPackageListProducer(PackageListProducer packageListProducer)
	{
		this.packageListProducer = packageListProducer;
	}

	/**
	 * Generates download links.
	 * 
	 * @param packageName
	 *          package name (or empty for all packages)
	 * @param showAll
	 *          <code>true</code> to show all versions
	 * @param model
	 *          MVC model object to populate
	 * @return download view
	 * @throws PackageListException
	 *           if an error occurs building the package list
	 */
	@RequestMapping("/download")
	public String download(
			@RequestParam(value = "packageName", required = false) String packageName,
			@RequestParam(value = "showAll", required = false) boolean showAll,
			Model model) throws PackageListException
	{
		List<Package> packages = packageListProducer.getPackages();

		if (StringUtils.isEmpty(packageName))
		{
			model.addAttribute("packages", packages);
		}
		else
		{
			List<Package> filtered = new ArrayList<Package>();
			for (Package pkg : packages)
			{
				if (packageName.equals(pkg.getName()))
					filtered.add(pkg);
			}
			model.addAttribute("packages", filtered);
			model.addAttribute("packageName", packageName);
		}
		model.addAttribute("showAll", showAll);
		model.addAttribute("maximumVersionCount", showAll ? Integer.MAX_VALUE : maximumVersionCount);

		return "download";
	}
}