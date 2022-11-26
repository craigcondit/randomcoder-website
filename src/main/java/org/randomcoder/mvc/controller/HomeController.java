package org.randomcoder.mvc.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.randomcoder.dao.Page;
import org.randomcoder.dao.Pagination;
import org.randomcoder.db.Article;
import org.randomcoder.mvc.command.ArticleListCommand;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

/**
 * Controller class which handles the front page of the site.
 */
@Controller("homeController")
public class HomeController extends AbstractArticleListController<ArticleListCommand> {

    @Override
    protected Page<Article> listArticlesBeforeDate(ArticleListCommand command, Date cutoffDate, long offset, long length) {
        return articleBusiness.listArticlesBeforeDate(cutoffDate, offset, length);
    }

    @Override
    protected List<Article> listArticlesBetweenDates(ArticleListCommand command, Date startDate, Date endDate) {
        return articleBusiness.listArticlesBetweenDates(startDate, endDate);
    }

    @Override
    protected String getSubTitle(ArticleListCommand command) {
        return null;
    }

    /**
     * Renders the home view.
     *
     * @param command  page command
     * @param model    MVC model
     * @param pageInfo paging parameters
     * @param request  HTTP servlet request
     * @return home view
     */
    @RequestMapping("/")
    public String home(ArticleListCommand command,
                       Model model,
                       Pagination pageInfo,
                       HttpServletRequest request) {
        populateModel(command, model, pageInfo, request);
        return "home";
    }

}
