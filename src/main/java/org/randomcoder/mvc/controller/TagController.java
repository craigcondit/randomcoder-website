package org.randomcoder.mvc.controller;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.*;
import org.randomcoder.bo.TagBusiness;
import org.randomcoder.mvc.command.*;
import org.randomcoder.mvc.validator.*;
import org.randomcoder.tag.TagStatistics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class which handles tag management.
 */
@Controller("tagController")
public class TagController
{
	private static final Log logger = LogFactory.getLog(TagController.class);

	private TagBusiness tagBusiness;
	private TagAddValidator tagAddValidator;
	private TagEditValidator tagEditValidator;
	private int defaultPageSize = 25;
	private int maximumPageSize = 100;

	/**
	 * Sets the TagBusiness implementation to use.
	 * 
	 * @param tagBusiness
	 *          TagBusiness implementation
	 */
	@Inject
	public void setTagBusiness(TagBusiness tagBusiness)
	{
		this.tagBusiness = tagBusiness;
	}

	/**
	 * Sets the validator to use for adding tags.
	 * 
	 * @param tagAddValidator
	 *          tag add validator
	 */
	@Inject
	public void setTagAddValidator(TagAddValidator tagAddValidator)
	{
		this.tagAddValidator = tagAddValidator;
	}

	/**
	 * Sets the validator to use for editing tags.
	 * 
	 * @param tagEditValidator
	 *          tag edit validator
	 */
	@Inject
	public void setTagEditValidator(TagEditValidator tagEditValidator)
	{
		this.tagEditValidator = tagEditValidator;
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
	 * Binds validators.
	 * 
	 * @param binder
	 *          data binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder)
	{
		Object target = binder.getTarget();
		if (target instanceof TagEditCommand)
		{
			binder.setValidator(tagEditValidator);
		}
		else if (target instanceof TagAddCommand)
		{
			binder.setValidator(tagAddValidator);
		}
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
	
	/**
	 * Begins adding a tag.
	 * 
	 * @param cmd
	 *          tag add command
	 * @param result
	 *          binding result
	 * @param model
	 *          model
	 * @return tag add view
	 */
	@RequestMapping(value = "/tag/add", method = RequestMethod.GET)
	public String addTag(@ModelAttribute("command") TagAddCommand cmd, BindingResult result, Model model)
	{
		model.addAttribute("command", new TagAddCommand());
		return "tag-add";
	}

	/**
	 * Cancels adding a new tag.
	 * 
	 * @return redirect to tag list view
	 */
	@RequestMapping(value = "/tag/add", method = RequestMethod.POST, params = "cancel")
	public String addTagCancel()
	{
		return "tag-list-redirect";
	}

	/**
	 * Saves a new tag.
	 * 
	 * @param cmd
	 *          tag add command
	 * @param result
	 *          validation result
	 * @return redirect to tag list view
	 */
	@RequestMapping(value = "/tag/add", method = RequestMethod.POST, params = "!cancel")
	public String addTagSubmit(@ModelAttribute("command") @Validated TagAddCommand cmd, BindingResult result)
	{
		if (result.hasErrors())
		{
			return "tag-add";
		}

		tagBusiness.createTag(cmd);

		return "tag-list-redirect";
	}

	/**
	 * Begins editing a tag.
	 * 
	 * @param cmd
	 *          tag edit command
	 * @param result
	 *          binding result
	 * @param model
	 *          model
	 * @return tag add view
	 */
	@RequestMapping(value = "/tag/edit", method = RequestMethod.GET)
	public String editTag(@ModelAttribute("command") TagEditCommand cmd, BindingResult result, Model model)
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("Command: " + cmd);
		}

		tagBusiness.loadTagForEditing(cmd, cmd.getId());
		model.addAttribute("command", cmd);
		return "tag-edit";
	}

	/**
	 * Cancels editing a new tag.
	 * 
	 * @return redirect to tag list view
	 */
	@RequestMapping(value = "/tag/edit", method = RequestMethod.POST, params = "cancel")
	public String editTagCancel()
	{
		return "tag-list-redirect";
	}

	/**
	 * Saves an edited tag.
	 * 
	 * @param cmd
	 *          tag edit command
	 * @param result
	 *          validation result
	 * @return redirect to tag list view
	 */
	@RequestMapping(value = "/tag/edit", method = RequestMethod.POST, params = "!cancel")
	public String editTagSubmit(@ModelAttribute("command") @Validated TagEditCommand cmd, BindingResult result)
	{
		if (result.hasErrors())
		{
			return "tag-edit";
		}

		if (logger.isDebugEnabled())
		{
			logger.debug("Command: " + cmd);
		}

		tagBusiness.updateTag(cmd, cmd.getId());
		return "tag-list-redirect";
	}

	/**
	 * Deletes the selected tag.
	 * 
	 * @param id
	 *          tag ID
	 * @return redirect to tag list view
	 */
	@RequestMapping("/tag/delete")
	public String deleteTag(@RequestParam("id") long id)
	{
		tagBusiness.deleteTag(id);
		return "tag-list-redirect";
	}
}