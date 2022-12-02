package org.randomcoder.website.command;

import jakarta.ws.rs.FormParam;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.model.TagList;

import java.util.function.Consumer;

public class ArticleEditCommand extends ArticleAddCommand implements Consumer<Article> {

    public void load(Article article) {
        super.load(article);
        this.id = article.getId();
    }

    private Long id;

    public Long getId() {
        return id;
    }

    @FormParam("id")
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void accept(Article article) {
        super.accept(article);
        setId(article.getId());
    }

}
