package org.randomcoder.bo;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.randomcoder.article.ArticleNotFoundException;
import org.randomcoder.article.comment.CommentNotFoundException;
import org.randomcoder.article.moderation.ModerationException;
import org.randomcoder.article.moderation.ModerationStatus;
import org.randomcoder.article.moderation.Moderator;
import org.randomcoder.db.Article;
import org.randomcoder.db.ArticleRepository;
import org.randomcoder.db.Comment;
import org.randomcoder.db.CommentIp;
import org.randomcoder.db.CommentIpRepository;
import org.randomcoder.db.CommentReferrer;
import org.randomcoder.db.CommentReferrerRepository;
import org.randomcoder.db.CommentRepository;
import org.randomcoder.db.CommentUserAgent;
import org.randomcoder.db.CommentUserAgentRepository;
import org.randomcoder.db.Role;
import org.randomcoder.db.RoleRepository;
import org.randomcoder.db.Tag;
import org.randomcoder.db.TagRepository;
import org.randomcoder.db.User;
import org.randomcoder.db.UserRepository;
import org.randomcoder.io.Consumer;
import org.randomcoder.io.Producer;
import org.randomcoder.security.UnauthorizedException;
import org.randomcoder.user.RoleNotFoundException;
import org.randomcoder.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Business implementation which handles articles.
 */
@Component("articleBusiness") public class ArticleBusinessImpl
    implements ArticleBusiness {
  private static final Logger logger =
      LoggerFactory.getLogger(ArticleBusinessImpl.class);

  private static final String ROLE_MANAGE_ARTICLES = "ROLE_MANAGE_ARTICLES";
  private static final String ROLE_MANAGE_COMMENTS = "ROLE_MANAGE_COMMENTS";

  private UserRepository userRepository;
  private RoleRepository roleRepository;
  private ArticleRepository articleRepository;
  private TagRepository tagRepository;
  private CommentRepository commentRepository;
  private CommentReferrerRepository commentReferrerRepository;
  private CommentIpRepository commentIpRepository;
  private CommentUserAgentRepository commentUserAgentRepository;
  private Moderator moderator;

  /**
   * Sets the user repository to use.
   *
   * @param userRepository user repository
   */
  @Inject public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Sets the role repository to use.
   *
   * @param roleRepository role repository
   */
  @Inject public void setRoleRepository(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  /**
   * Sets the article repository to use.
   *
   * @param articleRepository article repository
   */
  @Inject public void setArticleRepository(
      ArticleRepository articleRepository) {
    this.articleRepository = articleRepository;
  }

  /**
   * Sets the tag repository to use.
   *
   * @param tagRepository tag repository
   */
  @Inject public void setTagRepository(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  /**
   * Sets the comment repository to use.
   *
   * @param commentRepository comment repository
   */
  @Inject public void setCommentRepository(
      CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  /**
   * Sets the comment IP repository to use.
   *
   * @param commentIpRepository comment IP repository
   */
  @Inject public void setCommentIpRepository(
      CommentIpRepository commentIpRepository) {
    this.commentIpRepository = commentIpRepository;
  }

  /**
   * Sets the comment referrer repository to use.
   *
   * @param commentReferrerRepository comment referrer repository
   */
  @Inject public void setCommentReferrerRepository(
      CommentReferrerRepository commentReferrerRepository) {
    this.commentReferrerRepository = commentReferrerRepository;
  }

  /**
   * Sets the comment user agent repository to use.
   *
   * @param commentUserAgentRepository comment user agent repository
   */
  @Inject public void setCommentUserAgentRepository(
      CommentUserAgentRepository commentUserAgentRepository) {
    this.commentUserAgentRepository = commentUserAgentRepository;
  }

  /**
   * Sets the moderator to use for automatic comment moderation
   *
   * @param moderator comment moderator
   */
  @Inject public void setModerator(Moderator moderator) {
    this.moderator = moderator;
  }

  @Override @Transactional("transactionManager")
  public void createArticle(Producer<Article> producer, String userName) {
    User user = findUserByName(userName);

    Article article = new Article();

    producer.produce(article);

    article.setCreatedByUser(user);
    article.setCreationDate(new Date());

    List<Tag> tags = new ArrayList<>();
    for (Tag tag : article.getTags()) {
      if (tag.getId() == null) {
        tags.add(tagRepository.save(tag));
      } else {
        tags.add(tagRepository.getOne(tag.getId()));
      }
    }
    article.setTags(tags);

    articleRepository.save(article);
  }

  @Override @Transactional("transactionManager")
  public void createComment(Producer<Comment> producer, Long articleId,
      String userName, String referrer, String ipAddress, String userAgent) {
    User user = userName == null ? null : findUserByName(userName);

    Article article = loadArticle(articleId);

    if (!article.isCommentsEnabled()) {
      throw new IllegalArgumentException(
          "Comments are not enabled for this article");
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
      comment.setModerationStatus(
          trusted ? ModerationStatus.HAM : ModerationStatus.PENDING);
    }

    referrer = StringUtils.trimToNull(referrer);
    if (referrer != null) {
      CommentReferrer ref = commentReferrerRepository.findByUri(referrer);
      if (ref == null) {
        ref = new CommentReferrer();
        ref.setCreationDate(new Date());
        ref.setReferrerUri(referrer);
        commentReferrerRepository.save(ref);
      }
      comment.setReferrer(ref);
    }

    ipAddress = StringUtils.trimToNull(ipAddress);
    if (ipAddress != null) {
      CommentIp ip = commentIpRepository.findByIpAddress(ipAddress);
      if (ip == null) {
        ip = new CommentIp();
        ip.setCreationDate(new Date());
        ip.setIpAddress(ipAddress);
        commentIpRepository.save(ip);
      }
      comment.setIpAddress(ip);
    }

    userAgent = StringUtils.trimToNull(userAgent);
    if (userAgent != null) {
      CommentUserAgent ua = commentUserAgentRepository.findByName(userAgent);
      if (ua == null) {
        ua = new CommentUserAgent();
        ua.setCreationDate(new Date());
        ua.setUserAgentName(userAgent);
        commentUserAgentRepository.save(ua);
      }
      comment.setUserAgent(ua);
    }

    commentRepository.save(comment);

    article.getComments().add(comment);

    articleRepository.save(article);
  }

  @Override @Transactional("transactionManager")
  public void updateArticle(Producer<Article> producer, Long articleId,
      String userName) {
    User user = findUserByName(userName);

    Article article = loadArticle(articleId);

    checkAuthorUpdate(user, article);

    producer.produce(article);

    article.setModifiedByUser(user);
    article.setModificationDate(new Date());

    // save tags
    List<Tag> tags = new ArrayList<>();
    for (Tag tag : article.getTags()) {
      if (tag.getId() == null) {
        tags.add(tagRepository.save(tag));
      } else {
        tags.add(tagRepository.getOne(tag.getId()));
      }
    }
    article.setTags(tags);

    articleRepository.save(article);
  }

  @Override @Transactional(value = "transactionManager", readOnly = true)
  public Article readArticle(long articleId) {
    Article article = articleRepository.getOne(articleId);
    if (article != null) {
      Hibernate.initialize(article.getTags());
      Hibernate.initialize(article.getComments());
    }
    return article;
  }

  @Override @Transactional(value = "transactionManager", readOnly = true)
  public Article findArticleByPermalink(String permalink) {
    Article article = articleRepository.findByPermalink(permalink);
    if (article != null) {
      Hibernate.initialize(article.getTags());
      Hibernate.initialize(article.getComments());
    }
    return article;
  }

  @Override @Transactional(value = "transactionManager", readOnly = true)
  public void loadArticleForEditing(Consumer<Article> consumer, Long articleId,
      String userName) {
    User user = findUserByName(userName);
    Article article = loadArticle(articleId);
    Hibernate.initialize(article.getTags());
    Hibernate.initialize(article.getComments());

    checkAuthorUpdate(user, article);

    consumer.consume(article);
  }

  @Override @Transactional("transactionManager")
  public void deleteArticle(String userName, Long articleId) {
    User user = findUserByName(userName);
    Article article = loadArticle(articleId);

    checkAuthorDelete(user, article);

    articleRepository.delete(article);
  }

  @Override
  @Transactional(value = "transactionManager", rollbackFor = ModerationException.class)
  public Article approveComment(Long commentId) throws ModerationException {
    Comment comment = loadComment(commentId);

    logger.info("Approving comment #" + comment.getId());

    Article article = comment.getArticle();
    comment.setModerationStatus(ModerationStatus.HAM);
    comment.setVisible(true);
    commentRepository.save(comment);

    moderator.markAsHam(comment);

    return article;
  }

  @Override
  @Transactional(value = "transactionManager", rollbackFor = ModerationException.class)
  public Article disapproveComment(Long commentId) throws ModerationException {
    Comment comment = loadComment(commentId);

    logger.info("Disapproving comment #" + comment.getId());

    Article article = comment.getArticle();
    comment.setModerationStatus(ModerationStatus.SPAM);
    comment.setVisible(false);
    commentRepository.save(comment);

    moderator.markAsSpam(comment);

    return article;
  }

  @Override @Transactional("transactionManager")
  public Article deleteComment(Long commentId) {
    Comment comment = loadComment(commentId);

    logger.info("Deleting comment #" + comment.getId());

    Article article = comment.getArticle();

    article.getComments().remove(comment);

    commentRepository.delete(comment);
    articleRepository.save(article);

    return article;
  }

  @Override @Transactional("transactionManager")
  public boolean moderateComments(int count) throws ModerationException {
    Page<Comment> page = commentRepository
        .findForModeration(PageRequest.of(0, count, Sort.by("creationDate")));
    if (page.getNumberOfElements() < 1) {
      return false;
    }

    for (Comment comment : page.getContent()) {

      logger.info("Moderating comment #" + comment.getId());

      boolean valid = moderator.validate(comment);

      logger.info(valid ? "  HAM" : "  SPAM");

      comment.setVisible(valid);
      comment.setModerationStatus(
          valid ? ModerationStatus.HAM : ModerationStatus.SPAM);
      commentRepository.save(comment);
    }

    return true;
  }

  @Override @Transactional(value = "transactionManager", readOnly = true)
  public List<Article> listRecentArticles(int limit) {
    PageRequest req =
        PageRequest.of(0, limit, Sort.by(Direction.DESC, "creationDate"));
    List<Article> articles = articleRepository.findAll(req).getContent();
    Hibernate.initialize(articles);
    for (Article article : articles) {
      Hibernate.initialize(article.getTags());
      Hibernate.initialize(article.getComments());
    }
    return articles;
  }

  @Override @Transactional(value = "transactionManager", readOnly = true)
  public Page<Article> listArticlesBeforeDate(Date endDate, Pageable pageable) {
    Page<Article> articles =
        articleRepository.findBeforeDate(endDate, pageable);

    for (Article article : articles.getContent()) {
      Hibernate.initialize(article.getTags());
      Hibernate.initialize(article.getComments());
    }

    return articles;
  }

  @Override @Transactional(value = "transactionManager", readOnly = true)
  public Page<Article> listArticlesByTagBeforeDate(Tag tag, Date endDate,
      Pageable pageable) {
    Page<Article> articles =
        articleRepository.findByTagBeforeDate(tag, endDate, pageable);

    for (Article article : articles.getContent()) {
      Hibernate.initialize(article.getTags());
      Hibernate.initialize(article.getComments());
    }

    return articles;
  }

  @Override @Transactional(value = "transactionManager", readOnly = true)
  public List<Article> listArticlesBetweenDates(Date startDate, Date endDate) {
    return articleRepository.findBetweenDates(startDate, endDate);
  }

  @Override @Transactional(value = "transactionManager", readOnly = true)
  public List<Article> listArticlesByTagBetweenDates(Tag tag, Date startDate,
      Date endDate) {
    return articleRepository.findByTagBetweenDates(tag, startDate, endDate);
  }

  private void checkAuthorUpdate(User user, Article article) {
    checkAuthor(user, article,
        "You are not allowed to edit articles you did not create.");
  }

  private void checkAuthorDelete(User user, Article article) {
    checkAuthor(user, article,
        "You are not allowed to delete articles you did not create.");
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
    if (roles.contains(findRoleByName(ROLE_MANAGE_COMMENTS))) {
      return true;
    }

    return false;
  }

  private User findUserByName(String userName) throws UserNotFoundException {
    User user = userRepository.findByUserName(userName);
    if (user == null) {
      throw new UserNotFoundException("Unknown user: " + userName);
    }

    return user;
  }

  private Article loadArticle(Long articleId) throws ArticleNotFoundException {
    if (articleId == null) {
      throw new ArticleNotFoundException("Invalid id specified.");
    }

    Article article = articleRepository.getOne(articleId);
    if (article == null) {
      throw new ArticleNotFoundException(
          "No article exists with id: " + articleId);
    }

    return article;
  }

  private Comment loadComment(Long commentId) throws CommentNotFoundException {
    if (commentId == null) {
      throw new CommentNotFoundException("Invalid id specified.");
    }

    Comment comment = commentRepository.getOne(commentId);
    if (comment == null) {
      throw new CommentNotFoundException(
          "No comment exists with id: " + commentId);
    }

    return comment;
  }

  private Role findRoleByName(String roleName) throws RoleNotFoundException {
    Role role = roleRepository.findByName(roleName);

    if (role == null) {
      throw new RoleNotFoundException("Unknown role: " + roleName);
    }

    return role;
  }
}
