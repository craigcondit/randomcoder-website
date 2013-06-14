package org.randomcoder.controller;

import java.util.List;

import javax.inject.Inject;

import org.randomcoder.bo.TagBusiness;
import org.randomcoder.tag.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for tag lists.
 */
@Controller("tagListController")
public class TagListController
{
	private TagBusiness tagBusiness;
	private int defaultPageSize = 25;
	private int maximumPageSize = 100;

	/**
	 * Sets the TagBusiness implementation to use.
	 * 
	 * @param tagBusiness
	 *            TagBusiness implementation
	 */
	@Inject
	public void setTagBusiness(TagBusiness tagBusiness)
	{
		this.tagBusiness = tagBusiness;
	}

	/**
	 * Sets the default number of items to display per page (defaults to 25).
	 * 
	 * @param defaultPageSize
	 *            default number of items per page
	 */
	@Value("${tag.pagesize.default}")
	public void setDefaultPageSize(int defaultPageSize)
	{
		this.defaultPageSize = defaultPageSize;
	}

	/**
	 * Sets the maximum number of items to allow per page (defaults to 100).
	 * 
	 * @param maximumPageSize
	 *            maximum number of items per page
	 */
	@Value("${tag.pagesize.max}")
	public void setMaximumPageSize(int maximumPageSize)
	{
		this.maximumPageSize = maximumPageSize;
	}

	/**
	 * Generates the tag list.
	 * 
	 * @param cmd
	 *            form command
	 * @param model
	 *            MVC model
	 * @return tag list view
	 */
	@RequestMapping("/tag")
	public String tagList(TagListCommand cmd, Model model)
	{
		// set range
		int start = Math.max(cmd.getStart(), 0);
		cmd.setStart(start);

		int limit = cmd.getLimit();
		if (limit <= 0)
		{
			limit = defaultPageSize;
		}
		limit = Math.min(limit,  maximumPageSize);
		
		cmd.setLimit(limit);

		List<TagStatistics> tagStats = tagBusiness.queryTagStatisticsInRange(start, limit);
		int count = tagBusiness.countTags();

		// populate model
		model.addAttribute("tagStats", tagStats);
		model.addAttribute("pageCount", count);
		model.addAttribute("pageStart", start);
		model.addAttribute("pageLimit", limit);

		return "tag-list";
	}
}
