package org.randomcoder.mvc.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;
import org.randomcoder.dao.Page;
import org.randomcoder.dao.Pagination;
import org.randomcoder.db.Article;
import org.randomcoder.db.Tag;
import org.randomcoder.mvc.command.ArticleTagListCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Controller class which handles displaying articles by tag.
 */
@Controller("articleTagListController")
public class ArticleTagListController extends AbstractArticleListController<ArticleTagListCommand> {

    private static final Logger logger = LoggerFactory.getLogger(ArticleTagListController.class);

    @Override
    protected Page<Article> listArticlesBeforeDate(ArticleTagListCommand command, Date cutoffDate, long offset, long length) {
        return articleBusiness.listArticlesByTagBeforeDate(command.getTag(), cutoffDate, offset, length);
    }

    @Override
    protected List<Article> listArticlesBetweenDates(
            ArticleTagListCommand command, Date startDate, Date endDate) {
        return articleBusiness.listArticlesByTagBetweenDates(command.getTag(), startDate, endDate);
    }

    @Override
    protected String getSubTitle(ArticleTagListCommand command) {
        Tag tag = command.getTag();
        return tag == null ? null : tag.getDisplayName();
    }

    /**
     * Renders articles for a given tag.
     *
     * @param command  tag page command
     * @param tagName  tag name to display
     * @param model    MVC model
     * @param pageInfo paging variables
     * @param request  HTTP servlet request
     * @return home view
     */
    @RequestMapping("/tags/{tagName}")
    public String tagList(
            ArticleTagListCommand command, Model model,
            @PathVariable("tagName") String tagName,
            Pagination pageInfo,
            HttpServletRequest request) {
        tagName = StringUtils.trimToEmpty(tagName).toLowerCase(Locale.US);
        logger.debug("Tag name: " + tagName);
        command.setTag(tagBusiness.findTagByName(tagName));

        populateModel(command, model, pageInfo, request);

        return "article-tag-list";
    }
}