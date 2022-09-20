package org.randomcoder.mvc.controller;

import org.randomcoder.db.Article;
import org.randomcoder.mvc.command.ArticleListCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Controller class which handles the front page of the site.
 */
@Controller("homeController") public class HomeController
    extends AbstractArticleListController<ArticleListCommand> {

  @Override
  protected Page<Article> listArticlesBeforeDate(ArticleListCommand command,
      Date cutoffDate, Pageable pageable) {
    return articleBusiness.listArticlesBeforeDate(cutoffDate, pageable);
  }

  @Override
  protected List<Article> listArticlesBetweenDates(ArticleListCommand command,
      Date startDate, Date endDate) {
    return articleBusiness.listArticlesBetweenDates(startDate, endDate);
  }

  @Override protected String getSubTitle(ArticleListCommand command) {
    return null;
  }

  /**
   * Renders the home view.
   *
   * @param command  page command
   * @param model    MVC model
   * @param pageable paging parameters
   * @param request  HTTP servlet request
   * @return home view
   */
  @RequestMapping("/") public String home(ArticleListCommand command,
      Model model, @PageableDefault(10) Pageable pageable,
      HttpServletRequest request) {
    populateModel(command, model, pageable, request);
    return "home";
  }

}
