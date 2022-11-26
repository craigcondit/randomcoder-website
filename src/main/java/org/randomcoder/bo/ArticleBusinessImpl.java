package org.randomcoder.bo;

import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.randomcoder.article.ArticleNotFoundException;
import org.randomcoder.article.comment.CommentNotFoundException;
import org.randomcoder.article.moderation.ModerationException;
import org.randomcoder.article.moderation.ModerationStatus;
import org.randomcoder.article.moderation.Moderator;
import org.randomcoder.dao.ArticleDao;
import org.randomcoder.dao.CommentDao;
import org.randomcoder.dao.RoleDao;
import org.randomcoder.dao.TagDao;
import org.randomcoder.dao.UserDao;
import org.randomcoder.db.Article;
import org.randomcoder.db.Comment;
import org.randomcoder.db.Role;
import org.randomcoder.db.Tag;
import org.randomcoder.db.User;
import org.randomcoder.io.Consumer;
import org.randomcoder.io.Producer;
import org.randomcoder.security.UnauthorizedException;
import org.randomcoder.user.RoleNotFoundException;
import org.randomcoder.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Business implementation which handles articles.
 */
@Component("articleBusiness")
public class ArticleBusinessImpl implements ArticleBusiness {
    private static final Logger logger = LoggerFactory.getLogger(ArticleBusinessImpl.class);

    private static final String ROLE_MANAGE_ARTICLES = "ROLE_MANAGE_ARTICLES";
    private static final String ROLE_MANAGE_COMMENTS = "ROLE_MANAGE_COMMENTS";

    private ArticleDao articleDao;
    private CommentDao commentDao;
    private TagDao tagDao;
    private UserDao userDao;
    private RoleDao roleDao;
    private Moderator moderator;

    @Inject
    public void setArticleDao(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @Inject
    public void setCommentDao(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    @Inject
    public void setTagDao(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Inject
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Inject
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Inject
    public void setModerator(Moderator moderator) {
        this.moderator = moderator;
    }

    @Override
    public void createArticle(Producer<Article> producer, String userName) {
        User user = findUserByName(userName);

        Article article = new Article();

        producer.produce(article);

        article.setCreatedByUser(user);
        article.setCreationDate(new Date());

        for (Tag tag : article.getTags()) {
            if (tag.getId() == null) {
                tagDao.save(tag);
            }
        }

        articleDao.save(article);
    }

    @Override
    public void createComment(Producer<Comment> producer, Long articleId, String userName, String referrer, String ipAddress, String userAgent) {
        User user = userName == null ? null : findUserByName(userName);

        Article article = loadArticle(articleId);

        if (!article.isCommentsEnabled()) {
            throw new IllegalArgumentException("Comments are not enabled for this article");
        }

        Comment comment = new Comment();

        producer.produce(comment);

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
    public void updateArticle(Producer<Article> producer, Long articleId, String userName) {
        User user = findUserByName(userName);

        Article article = loadArticle(articleId);

        checkAuthorUpdate(user, article);

        producer.produce(article);

        article.setModifiedByUser(user);
        article.setModificationDate(new Date());

        // save tags
        for (Tag tag : article.getTags()) {
            if (tag.getId() == null) {
                tagDao.save(tag);
            }
        }
        articleDao.save(article);
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
    public void loadArticleForEditing(Consumer<Article> consumer, Long articleId, String userName) {
        User user = findUserByName(userName);
        Article article = loadArticle(articleId);

        checkAuthorUpdate(user, article);

        consumer.consume(article);
    }

    @Override
    public void deleteArticle(String userName, Long articleId) {
        User user = findUserByName(userName);
        Article article = loadArticle(articleId);
        checkAuthorDelete(user, article);
        articleDao.deleteById(articleId);
    }

    @Override
    public Article approveComment(Long commentId) throws ModerationException {
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
    public Article disapproveComment(Long commentId) throws ModerationException {
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
    public Article deleteComment(Long commentId) {
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
        var page = articleDao.listByDateDesc(0, limit);
        return page.getContent();
    }

    @Override
    public Page<Article> listArticlesBeforeDate(Date endDate, Pageable pageable) {
        var articles = articleDao.listBeforeDate(endDate, pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(articles.getContent(), pageable, articles.getTotalSize());
    }

    @Override
    public Page<Article> listArticlesByTagBeforeDate(Tag tag, Date endDate, Pageable pageable) {
        var articles = articleDao.listByTagBeforeDate(tag, endDate, pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(articles.getContent(), pageable, articles.getTotalSize());
    }

    @Override
    public List<Article> listArticlesBetweenDates(Date startDate, Date endDate) {
        return articleDao.listBetweenDates(startDate, endDate);
    }

    @Override
    public List<Article> listArticlesByTagBetweenDates(Tag tag, Date startDate, Date endDate) {
        return articleDao.listByTagBetweenDates(tag, startDate, endDate);
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
                throw new UnauthorizedException(errorMessage);
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