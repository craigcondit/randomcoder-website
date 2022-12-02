package org.randomcoder.website.validation;

import jakarta.inject.Inject;
import org.randomcoder.website.command.CommentCommand;
import org.randomcoder.website.contentfilter.ContentFilter;
import org.randomcoder.website.contentfilter.InvalidContentException;
import org.randomcoder.website.contentfilter.InvalidContentTypeException;
import org.randomcoder.website.data.ContentType;
import org.randomcoder.website.io.SequenceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CommentValidator {

    private static final String ERROR_COMMENT_USER_NAME_FORBIDDEN = "error.comment.username.forbidden";
    private static final String ERROR_COMMENT_USER_NAME_REQUIRED = "error.comment.username.required";
    private static final String ERROR_COMMENT_EMAIL_ADDRESS_FORBIDDEN = "error.comment.emailaddress.forbidden";
    private static final String ERROR_COMMENT_EMAIL_ADDRESS_REQUIRED = "error.comment.emailaddress.required";
    private static final String ERROR_COMMENT_EMAIL_ADDRESS_INVALID = "error.comment.emailaddress.invalid";
    private static final String ERROR_COMMENT_WEBSITE_FORBIDDEN = "error.comment.website.forbidden";
    private static final String ERROR_COMMENT_WEBSITE_INVALID = "error.comment.website.invalid";
    private static final String ERROR_COMMENT_TITLE_REQUIRED = "error.comment.title.required";
    private static final String ERROR_COMMENT_CONTENT_REQUIRED = "error.comment.content.required";
    private static final String ERROR_COMMENT_CONTENT_INVALID = "error.comment.content.invalid";

    private static final Logger logger = LoggerFactory.getLogger(CommentValidator.class);

    @Inject
    ContentFilter contentFilter;

    public void validate(ValidatorContext context, CommentCommand command) {
        if (command.isAnonymous()) {
            // validate anonymous input fields

            String username = command.getAnonymousUserName();
            if (username == null || username.trim().length() == 0) {
                context.reject("anonymousUserName", ERROR_COMMENT_USER_NAME_REQUIRED);
            }

            String emailAddress = command.getAnonymousEmailAddress();
            if (emailAddress == null) {
                context.reject("anonymousEmailAddress", ERROR_COMMENT_EMAIL_ADDRESS_REQUIRED);
            } else if (!DataValidationUtils.isValidEmailAddress(emailAddress)) {
                context.reject("anonymousEmailAddress", ERROR_COMMENT_EMAIL_ADDRESS_INVALID);
            }

            String website = command.getAnonymousWebsite();
            if (website != null && !DataValidationUtils.isValidUrl(website)) {
                context.reject("anonymousWebsite", ERROR_COMMENT_WEBSITE_INVALID);
            }
        } else {
            // make sure they aren't specified
            if (command.getAnonymousUserName() != null) {
                context.reject("global", ERROR_COMMENT_USER_NAME_FORBIDDEN);
            }
            if (command.getAnonymousEmailAddress() != null) {
                context.reject("global", ERROR_COMMENT_EMAIL_ADDRESS_FORBIDDEN);
            }
            if (command.getAnonymousWebsite() != null) {
                context.reject("global", ERROR_COMMENT_WEBSITE_FORBIDDEN);
            }
        }

        String title = command.getTitle();
        if (title == null) {
            context.reject("title", ERROR_COMMENT_TITLE_REQUIRED);
        }

        // TODO implement support for other content types
        ContentType contentType = ContentType.TEXT;

        String content = command.getContent();
        if (content == null || content.trim().length() == 0) {
            context.reject("content", ERROR_COMMENT_CONTENT_REQUIRED);
        } else if (contentType != null) {
            // validate the content
            String mimeType = contentType.getMimeType();

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
                context.reject("content", ERROR_COMMENT_CONTENT_INVALID, line, col, e.getMessage());
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

}
