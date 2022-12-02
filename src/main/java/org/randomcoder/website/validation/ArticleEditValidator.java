package org.randomcoder.website.validation;

import org.randomcoder.website.command.ArticleEditCommand;
import org.randomcoder.website.data.Article;

public class ArticleEditValidator extends ArticleAddValidator {

    private static final String ERROR_ARTICLE_ID_REQUIRED = "error.article.id.required";

    public void validate(ValidatorContext context, ArticleEditCommand command) {
        validateCommon(context, command);

        Long id = command.getId();

        if (id == null) {
            context.reject("id", ERROR_ARTICLE_ID_REQUIRED);
        }

        if (!context.getErrors().containsKey("permalink")) {
            String permalink = command.getPermalink();
            if (permalink != null) {
                // look for article with the same permalink
                Article prev = articleBusiness.findArticleByPermalink(permalink);

                if (prev != null && !prev.getId().equals(id)) {
                    context.reject("permalink", ERROR_ARTICLE_PERMALINK_EXISTS, "permalink exists");
                }
            }
        }
    }

}
