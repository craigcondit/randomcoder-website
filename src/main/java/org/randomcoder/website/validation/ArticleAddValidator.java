package org.randomcoder.website.validation;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.randomcoder.website.Config;
import org.randomcoder.website.bo.ArticleBusiness;
import org.randomcoder.website.command.ArticleAddCommand;
import org.randomcoder.website.contentfilter.ContentFilter;
import org.randomcoder.website.contentfilter.InvalidContentException;
import org.randomcoder.website.contentfilter.InvalidContentTypeException;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.ContentType;
import org.randomcoder.website.data.Tag;
import org.randomcoder.website.io.SequenceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ArticleAddValidator {
    protected static final String ERROR_ARTICLE_PERMALINK_EXISTS = "error.article.permalink.exists";
    private static final String ERROR_ARTICLE_CONTENT_INVALID = "error.article.content.invalid";
    private static final String ERROR_ARTICLE_CONTENT_REQUIRED = "error.article.content.required";
    private static final String ERROR_ARTICLE_SUMMARY_INVALID = "error.article.summary.invalid";
    private static final String ERROR_ARTICLE_SUMMARY_TOO_LONG = "error.article.summary.toolong";
    private static final String ERROR_ARTICLE_CONTENT_TYPE_REQUIRED = "error.article.contentType.required";
    private static final String ERROR_ARTICLE_TITLE_REQUIRED = "error.article.title.required";
    private static final String ERROR_ARTICLE_PERMALINK_INVALID = "error.article.permalink.invalid";
    private static final int DEFAULT_MAX_SUMMARY_LENGTH = 1000;

    private static final Logger logger = LoggerFactory.getLogger(ArticleAddValidator.class);

    @Inject
    ArticleBusiness articleBusiness;

    @Inject
    ContentFilter contentFilter;

    @Inject
    @Named(Config.ARTICLE_MAX_SUMMARY_LENGTH)
    int maximumSummaryLength = DEFAULT_MAX_SUMMARY_LENGTH;

    public void validate(ValidatorContext context, ArticleAddCommand command) {
        validateCommon(context, command);

        if (!context.getErrors().containsKey("permalink")) {
            String permalink = command.getPermalink();
            if (permalink != null) {
                // look for article with the same permalink
                Article prev = articleBusiness.findArticleByPermalink(permalink);
                if (prev != null) {
                    context.reject("permalink", ERROR_ARTICLE_PERMALINK_EXISTS);
                }
            }
        }
    }

    protected final void validateCommon(ValidatorContext context, ArticleAddCommand command) {
        String title = command.getTitle();
        if (title == null || title.trim().length() == 0) {
            context.reject("title", ERROR_ARTICLE_TITLE_REQUIRED);
        }

        String permalink = command.getPermalink();
        if (permalink != null && !permalink.matches("[a-z0-9\\-]+")) {
            context.reject("permalink", ERROR_ARTICLE_PERMALINK_INVALID);
        }

        ContentType contentType = command.getContentType();
        if (contentType == null) {
            context.reject("contentType", ERROR_ARTICLE_CONTENT_TYPE_REQUIRED);
        }

        String content = command.getContent();
        if (content == null || content.trim().length() == 0) {
            context.reject("content", ERROR_ARTICLE_CONTENT_REQUIRED);
        } else if (contentType != null) {
            // validate the content
            validateContent(context, content, contentType.getMimeType(), "content", ERROR_ARTICLE_CONTENT_INVALID);
        }

        for (Tag tag : command.getTags().getTags()) {
            String name = DataValidationUtils.canonicalizeTagName(tag.getName());
            tag.setName(name);
        }

        String summary = command.getSummary();
        if (summary != null) {
            // validate summary
            if (summary.length() > maximumSummaryLength) {
                context.reject("summary", ERROR_ARTICLE_SUMMARY_TOO_LONG, maximumSummaryLength);
            } else if (contentType != null) {
                validateContent(context, summary, contentType.getMimeType(), "summary", ERROR_ARTICLE_SUMMARY_INVALID);
            }
        }
    }

    private void validateContent(ValidatorContext context, String content, String mimeType,
                                 String fieldName, String errorMessage) {
        String prefix = contentFilter.getPrefix(mimeType);
        String suffix = contentFilter.getSuffix(mimeType);

        List<Reader> readers = new ArrayList<Reader>();
        if (prefix != null) {
            readers.add(new StringReader(prefix));
        }
        readers.add(new StringReader(content));
        if (suffix != null) {
            readers.add(new StringReader(suffix));
        }

        SequenceReader reader = new SequenceReader(readers);

        try {
            contentFilter.validate(mimeType, reader);
        } catch (InvalidContentException e) {
            int line = e.getLineNumber();
            int col = e.getColumnNumber();

            context.reject(fieldName, errorMessage, line, col, e.getMessage());
        } catch (InvalidContentTypeException e) {
            logger.error("Caught exception", e);
            throw new RuntimeException("Invalid content type", e);
        } catch (IOException e) {
            // this shouldn't happen, since all readers are string readers
            logger.error("Caught exception", e);
            throw new RuntimeException("I/O error", e);
        }
    }

}
