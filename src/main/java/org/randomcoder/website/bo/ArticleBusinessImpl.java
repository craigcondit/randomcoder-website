package org.randomcoder.website.bo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotAuthorizedException;
import org.apache.commons.lang3.StringUtils;
import org.randomcoder.website.cache.ArticleCache;
import org.randomcoder.website.cache.ArticlesBeforeDateRangeKey;
import org.randomcoder.website.cache.ArticlesBetweenDatesKey;
import org.randomcoder.website.cache.ArticlesByTagBeforeDateRangeKey;
import org.randomcoder.website.cache.ArticlesByTagBetweenDatesKey;
import org.randomcoder.website.cache.TagCache;
import org.randomcoder.website.dao.ArticleDao;
import org.randomcoder.website.dao.CommentDao;
import org.randomcoder.website.dao.RoleDao;
import org.randomcoder.website.dao.TagDao;
import org.randomcoder.website.dao.UserDao;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.ArticleNotFoundException;
import org.randomcoder.website.data.Comment;
import org.randomcoder.website.data.CommentNotFoundException;
import org.randomcoder.website.data.ModerationException;
import org.randomcoder.website.data.ModerationStatus;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.Role;
import org.randomcoder.website.data.RoleNotFoundException;
import org.randomcoder.website.data.Tag;
import org.randomcoder.website.data.User;
import org.randomcoder.website.data.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@Singleton
public class ArticleBusinessImpl implements ArticleBusiness {
    private static final Logger logger = LoggerFactory.getLogger(ArticleBusinessImpl.class);

    private static final String ROLE_MANAGE_ARTICLES = "ROLE_MANAGE_ARTICLES";
    private static final String ROLE_MANAGE_COMMENTS = "ROLE_MANAGE_COMMENTS";

    @Inject
    ArticleDao articleDao;

    @Inject
    CommentDao commentDao;

    @Inject
    TagDao tagDao;

    @Inject
    UserDao userDao;

    @Inject
    RoleDao roleDao;

    @Inject
    Moderator moderator;

    @Inject
    ArticleCache articleCache;

    @Inject
    TagCache tagCache;

    @Override
    public Article createArticle(Consumer<Article> visitor, String userName) {
        User user = findUserByName(userName);

        Article article = new Article();
        visitor.accept(article);

        article.setCreatedByUser(user);
        article.setCreationDate(new Date());

        for (Tag tag : article.getTags()) {
            if (tag.getId() == null) {
                tagDao.save(tag);
            }
        }

        long id = articleDao.save(article);
        article.setId(id);

        articleCache.clearAll();
        tagCache.clearAll();

        return article;
    }

    @Override
    public void createComment(Consumer<Comment> visitor, long articleId, String userName, String referrer, String ipAddress, String userAgent) {
        User user = userName == null ? null : findUserByName(userName);

        Article article = loadArticle(articleId);

        if (!article.isCommentsEnabled()) {
            throw new IllegalArgumentException("Comments are not enabled for this article");
        }

        Comment comment = new Comment();
        visitor.accept(comment);

        comment.setCreatedByUser(user);
        comment.setCreationDate(new Date());
        comment.setArticle(article);

        if (user == null) {
            comment.setVisible(false); // anonymous users have to pass the
            // checks
            comment.setModerationStatus(ModerationStatus.PENDING);
        } else {
            comment.setVisible(true); // allow comment to display initially
            boolean trusted = isUserTrustedForComments(user);
            comment.setModerationStatus(trusted ? ModerationStatus.HAM : ModerationStatus.PENDING);
        }

        comment.setReferrer(StringUtils.trimToNull(referrer));
        comment.setIpAddress(StringUtils.trimToNull(ipAddress));
        comment.setUserAgent(StringUtils.trimToNull(userAgent));

        commentDao.save(comment);
    }

    @Override
    public Article updateArticle(Consumer<Article> visitor, long articleId, String userName) {
        User user = findUserByName(userName);

        Article article = loadArticle(articleId);
        checkAuthorUpdate(user, article);
        visitor.accept(article);

        article.setModifiedByUser(user);
        article.setModificationDate(new Date());

        // save tags
        for (Tag tag : article.getTags()) {
            if (tag.getId() == null) {
                tagDao.save(tag);
            }
        }

        articleDao.save(article);

        articleCache.clearAll();
        tagCache.clearAll();

        return article;
    }

    @Override
    public Article readArticle(long articleId) {
        return articleDao.findById(articleId);
    }

    @Override
    public Article findArticleByPermalink(String permalink) {
        return articleDao.findByPermalink(permalink);
    }

    @Override
    public void loadArticleForEditing(Consumer<Article> consumer, long articleId, String userName) {
        User user = findUserByName(userName);
        Article article = loadArticle(articleId);

        checkAuthorUpdate(user, article);
        consumer.accept(article);
    }

    @Override
    public void deleteArticle(String userName, long articleId) {
        User user = findUserByName(userName);
        Article article = loadArticle(articleId);
        checkAuthorDelete(user, article);
        articleDao.deleteById(articleId);
        articleCache.clearAll();
        tagCache.clearAll();
    }

    @Override
    public Article approveComment(long commentId) throws ModerationException {
        Comment comment = loadComment(commentId);

        logger.info("Approving comment #" + comment.getId());

        Article article = comment.getArticle();
        comment.setModerationStatus(ModerationStatus.HAM);
        comment.setVisible(true);
        commentDao.save(comment);

        moderator.markAsHam(comment);

        return article;
    }

    @Override
    public Article disapproveComment(long commentId) throws ModerationException {
        Comment comment = loadComment(commentId);

        logger.info("Disapproving comment #" + comment.getId());

        Article article = comment.getArticle();
        comment.setModerationStatus(ModerationStatus.SPAM);
        comment.setVisible(false);
        commentDao.save(comment);

        moderator.markAsSpam(comment);

        return article;
    }

    @Override
    public Article deleteComment(long commentId) {
        Comment comment = loadComment(commentId);

        logger.info("Deleting comment #" + comment.getId());

        Article article = comment.getArticle();
        commentDao.deleteById(comment.getId());

        return article;
    }

    @Override
    public boolean moderateComments(int count) throws ModerationException {
        var page = commentDao.listForModeration(0, count);
        if (page.getContent().size() < 1) {
            return false;
        }
        for (Comment comment : page.getContent()) {
            logger.info("Moderating comment #" + comment.getId());

            boolean valid = moderator.validate(comment);

            logger.info(valid ? "  HAM" : "  SPAM");
            comment.setVisible(valid);
            comment.setModerationStatus(valid ? ModerationStatus.HAM : ModerationStatus.SPAM);
            commentDao.save(comment);
        }

        return true;
    }

    @Override
    public List<Article> listRecentArticles(int limit) {
        return articleCache.articlesRecentLimit().get(limit, k -> {
            var page = articleDao.listByDateDesc(0, limit);
            return page.getContent();
        });
    }

    @Override
    public Page<Article> listArticlesBeforeDate(Date endDate, long offset, long length) {
        var cacheKey = new ArticlesBeforeDateRangeKey(endDate, offset, length);
        return articleCache.articlesBeforeDateRange().get(cacheKey, k ->
                articleDao.listBeforeDate(endDate, offset, length));
    }

    @Override
    public Page<Article> listArticlesByTagBeforeDate(Tag tag, Date endDate, long offset, long length) {
        var cacheKey = new ArticlesByTagBeforeDateRangeKey(tag, endDate, offset, length);
        return articleCache.articlesByTagBeforeDateRange().get(cacheKey, k ->
                articleDao.listByTagBeforeDate(tag, endDate, offset, length));
    }

    @Override
    public List<Article> listArticlesBetweenDates(Date startDate, Date endDate) {
        var cacheKey = new ArticlesBetweenDatesKey(startDate, endDate);
        return articleCache.articlesBetweenDates().get(cacheKey, k ->
                articleDao.listBetweenDates(startDate, endDate));
    }

    @Override
    public List<Article> listArticlesByTagBetweenDates(Tag tag, Date startDate, Date endDate) {
        var cacheKey = new ArticlesByTagBetweenDatesKey(tag, startDate, endDate);
        return articleCache.articlesByTagBetweenDates().get(cacheKey, k ->
                articleDao.listByTagBetweenDates(tag, startDate, endDate));
    }

    private void checkAuthorUpdate(User user, Article article) {
        checkAuthor(user, article, "You are not allowed to edit articles you did not create.");
    }

    private void checkAuthorDelete(User user, Article article) {
        checkAuthor(user, article, "You are not allowed to delete articles you did not create.");
    }

    private void checkAuthor(User user, Article article, String errorMessage) {
        Role articleAdmin = findRoleByName(ROLE_MANAGE_ARTICLES);

        if (!user.getRoles().contains(articleAdmin)) {
            // make sure user created this article
            User createdBy = article.getCreatedByUser();

            if (createdBy == null || !user.getId().equals(createdBy.getId())) {
                throw new NotAuthorizedException(errorMessage);
            }
        }
    }

    private boolean isUserTrustedForComments(User user) {
        if (user == null) {
            return false; // anonymous users are not trusted
        }

        List<Role> roles = user.getRoles();

        // trust users who can post / modify articles
        if (roles.contains(findRoleByName(ROLE_MANAGE_ARTICLES))) {
            return true;
        }

        // trust users who can modify comments
        return roles.contains(findRoleByName(ROLE_MANAGE_COMMENTS));
    }

    private User findUserByName(String userName) throws UserNotFoundException {
        User user = userDao.findByName(userName, true);
        if (user == null) {
            throw new UserNotFoundException("Unknown user: " + userName);
        }
        return user;
    }

    private Article loadArticle(Long articleId) throws ArticleNotFoundException {
        if (articleId == null) {
            throw new ArticleNotFoundException("Invalid id specified.");
        }
        Article article = articleDao.findById(articleId);
        if (article == null) {
            throw new ArticleNotFoundException("No article exists with id: " + articleId);
        }

        return article;
    }

    private Comment loadComment(Long commentId) throws CommentNotFoundException {
        if (commentId == null) {
            throw new CommentNotFoundException("Invalid id specified.");
        }

        Comment comment = commentDao.findById(commentId);
        if (comment == null) {
            throw new CommentNotFoundException("No comment exists with id: " + commentId);
        }

        return comment;
    }

    private Role findRoleByName(String roleName) throws RoleNotFoundException {
        Role role = roleDao.findByName(roleName);

        if (role == null) {
            throw new RoleNotFoundException("Unknown role: " + roleName);
        }

        return role;
    }

}