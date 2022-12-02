package org.randomcoder.website.controller;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.randomcoder.website.command.CommentCommand;
import org.randomcoder.website.contentfilter.ContentFilter;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.ContentType;
import org.randomcoder.website.model.ArticleDecorator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ArticleController {

    @Inject
    ContentFilter contentFilter;

    public Map<String, Object> buildModel(CommentCommand command, Article article) {
        return buildModel(command, article, null);
    }

    public Map<String, Object> buildModel(CommentCommand command, Article article, Map<String, List<String>> errors) {
        if (errors == null) {
            errors = new HashMap<>();
        }

        var model = new HashMap<String, Object>();
        var wrappedArticles = new ArrayList<ArticleDecorator>(1);
        wrappedArticles.add(new ArticleDecorator(article, contentFilter));
        model.put("articles", wrappedArticles);
        model.put("pageSubTitle", article.getTitle());
        model.put("commentsEnabled", article.isCommentsEnabled());
        model.put("command", command);
        model.put("contentTypes", ContentType.values());
        model.put("articlePermalink", article.getPermalinkUrl());
        model.put("errors", errors);
        return model;
    }

}
