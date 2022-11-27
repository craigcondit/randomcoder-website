package org.randomcoder.website.dao;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.randomcoder.website.model.ModerationStatus;
import org.randomcoder.website.model.ContentType;
import org.randomcoder.website.model.Article;
import org.randomcoder.website.model.Comment;
import org.randomcoder.website.model.Page;
import org.randomcoder.website.model.Tag;
import org.randomcoder.website.model.User;
import org.randomcoder.website.func.UncheckedConsumer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.randomcoder.website.dao.DaoUtils.withReadonlyConnection;
import static org.randomcoder.website.dao.DaoUtils.withTransaction;

@Singleton
public class ArticleDaoImpl implements ArticleDao {

    private DataSource dataSource;

    @Inject
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String INSERT = """
        INSERT INTO articles (
            content_type, create_user_id, create_date, modify_user_id, modify_date,
            title, permalink, content, summary, comments_enabled)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING article_id""";

    private static final String UPDATE = """
        UPDATE articles SET
            content_type = ?, create_user_id = ?, create_date = ?, modify_user_id = ?, modify_date = ?,
            title = ?, permalink = ?, content = ?, summary = ?, comments_enabled = ?
        WHERE article_id = ?""";

    private static final String SELECT_ALL = """
            SELECT
                a.article_id article_id,
                a.content_type content_type,
                a.create_user_id create_user_id,
                cu.username create_username,
                cu.email create_email,
                cu.website create_website,
                a.create_date create_date,
                a.modify_user_id modify_user_id,
                mu.username modify_username,
                mu.email modify_email,
                mu.website modify_website,
                a.modify_date modify_date,
                a.title title,
                a.permalink permalink,
                a.content content,
                a.summary summary,
                a.comments_enabled comments_enabled
            FROM articles a
            LEFT OUTER JOIN users cu ON a.create_user_id = cu.user_id
            LEFT OUTER JOIN users mu ON a.modify_user_id = mu.user_id""";

    private static final String SELECT_TAGS_BY_ARTICLE_IDS = """
            SELECT
                atl.article_id article_id, t.tag_id tag_id, t.name name, t.display_name display_name
            FROM tags t
            JOIN article_tag_link atl ON atl.tag_id = t.tag_id
            WHERE atl.article_id = ANY (?)
            ORDER BY t.display_name""";

    private static final String DELETE_BY_ID = "DELETE FROM articles WHERE article_id = ?";

    private static final String FIND_BY_ID = SELECT_ALL + " WHERE a.article_id = ?";
    private static final String FIND_BY_PERMALINK = SELECT_ALL + " WHERE a.permalink = ?";

    private static final String COUNT_BEFORE_DATE =
            "SELECT count(1) FROM articles WHERE create_date < ?";

    private static final String LIST_PAGED = SELECT_ALL + " " + """
            ORDER BY a.create_date DESC
            OFFSET ? LIMIT ?""";

    private static final String COUNT_ALL = "SELECT count(1) FROM articles";

    private static final String LIST_BEFORE_DATE_PAGED = SELECT_ALL + " " + """
            WHERE a.create_date < ?
            ORDER BY a.create_date DESC
            OFFSET ? LIMIT ?""";

    private static final String COUNT_BY_TAG_BEFORE_DATE = """
            SELECT count(1)
            FROM articles a
            JOIN article_tag_link atl ON a.article_id = atl.article_id AND atl.tag_id = ?
            WHERE a.create_date < ?""";

    private static final String LIST_BY_TAG_BEFORE_DATE_PAGED = SELECT_ALL + " " + """
            JOIN article_tag_link atl ON a.article_id = atl.article_id AND atl.tag_id = ?
            WHERE a.create_date < ?
            ORDER BY a.create_date DESC
            OFFSET ? LIMIT ?""";

    private static final String LIST_BETWEEN_DATES = SELECT_ALL + " " + """
            WHERE a.create_date >= ? AND a.create_date < ?
            ORDER BY a.create_date DESC""";

    private static final String LIST_BY_TAG_BETWEEN_DATES = SELECT_ALL + " " + """
            JOIN article_tag_link atl ON a.article_id = atl.article_id AND atl.tag_id = ?
            WHERE a.create_date >= ? AND a.create_date < ?
            ORDER BY a.create_date DESC""";

    private static final String SELECT_COMMENTS_BY_ARTICLE_IDS = """
            SELECT
                c.article_id article_id,
                c.comment_id comment_id,
                c.content_type content_type,
                c.create_user_id create_user_id,
                cu.username create_username,
                cu.email create_email,
                cu.website create_website,
                c.create_date create_date,
                c.anonymous_user_name anonymous_user_name,
                c.anonymous_email_address anonymous_email_address,
                c.anonymous_website anonymous_website,
                c.title title,
                c.content content,
                c.visible visible,
                c.moderation_status moderation_status,
                c.ip_address ip_address,
                c.referrer referrer,
                c.user_agent user_agent
            FROM comments c
            LEFT JOIN users cu ON cu.user_id = c.create_user_id
            WHERE c.article_id = ANY (?)
            ORDER BY c.comment_id, c.article_id""";

    private static final String DELETE_TAG_LINK_BY_ARTICLE_ID = """
            DELETE FROM article_tag_link WHERE article_id = ?""";

    private static final String INSERT_TAG_LINK = """
            INSERT INTO article_tag_link (article_id, tag_id) VALUES (?,?)""";

    private static final String COL_ARTICLE_ID = "article_id";
    private static final String COL_CONTENT_TYPE = "content_type";
    private static final String COL_CREATE_USER_ID = "create_user_id";
    private static final String COL_CREATE_USERNAME = "create_username";
    private static final String COL_CREATE_EMAIL = "create_email";
    private static final String COL_CREATE_WEBSITE = "create_website";
    private static final String COL_CREATE_DATE = "create_date";
    private static final String COL_MODIFY_USER_ID = "modify_user_id";
    private static final String COL_MODIFY_USERNAME = "modify_username";
    private static final String COL_MODIFY_EMAIL = "modify_email";
    private static final String COL_MODIFY_WEBSITE = "modify_website";
    private static final String COL_MODIFY_DATE = "modify_date";
    private static final String COL_TITLE = "title";
    private static final String COL_PERMALINK = "permalink";
    private static final String COL_CONTENT = "content";
    private static final String COL_SUMMARY = "summary";
    private static final String COL_COMMENTS_ENABLED = "comments_enabled";

    private static final String COL_TAG_ID = "tag_id";
    private static final String COL_TAG_NAME = "name";
    private static final String COL_TAG_DISPLAY_NAME = "display_name";

    private static final String COL_COMMENT_ID = "comment_id";
    private static final String COL_COMMENT_CONTENT_TYPE = "content_type";
    private static final String COL_COMMENT_CREATE_USER_ID = "create_user_id";
    private static final String COL_COMMENT_CREATE_USERNAME = "create_username";
    private static final String COL_COMMENT_CREATE_EMAIL = "create_email";
    private static final String COL_COMMENT_CREATE_WEBSITE = "create_website";
    private static final String COL_COMMENT_CREATE_DATE = "create_date";
    private static final String COL_COMMENT_ANON_USER_NAME = "anonymous_user_name";
    private static final String COL_COMMENT_ANON_EMAIL_ADDRESS = "anonymous_email_address";
    private static final String COL_COMMENT_ANON_WEBSITE = "anonymous_website";
    private static final String COL_COMMENT_TITLE = "title";
    private static final String COL_COMMENT_CONTENT = "content";
    private static final String COL_COMMENT_VISIBLE = "visible";
    private static final String COL_COMMENT_MODERATION_STATUS = "moderation_status";
    private static final String COL_COMMENT_IP_ADDRESS = "ip_address";
    private static final String COL_COMMENT_REFERRER = "referrer";
    private static final String COL_COMMENT_USER_AGENT = "user_agent";

    @Override
    public Long save(Article article) {
        return withTransaction(dataSource, con -> (article.getId() == null)
                ? createArticle(con, article)
                : updateArticle(con, article));
    }

    @Override
    public void deleteById(long articleId) {
        withTransaction(dataSource, con -> {
            try (PreparedStatement ps = con.prepareStatement(DELETE_BY_ID)) {
                ps.setLong(1, articleId);
                ps.executeUpdate();
            }
        });
    }

    @Override
    public Article findById(long articleId) {
        return withReadonlyConnection(dataSource, con -> {
            return loadArticle(con, FIND_BY_ID, ps -> {
                ps.setLong(1, articleId);
            });
        });
    }

    @Override
    public Article findByPermalink(String permalink) {
        return withReadonlyConnection(dataSource, con -> {
            return loadArticle(con, FIND_BY_PERMALINK, ps -> {
                ps.setString(1, permalink);
            });
        });
    }

    @Override
    public Page<Article> listByDateDesc(long offset, long length) {
        return withReadonlyConnection(dataSource, con -> {
            return loadArticlesPaged(
                    con, offset, length, COUNT_ALL, LIST_PAGED,
                    ps -> {},
                    ps -> {
                        ps.setLong(1, offset);
                        ps.setLong(2, length);
                    });
        });
    }

    @Override
    public Page<Article> listBeforeDate(Date endDate, long offset, long length) {
        return withReadonlyConnection(dataSource, con -> {
            return loadArticlesPaged(
                    con, offset, length, COUNT_BEFORE_DATE, LIST_BEFORE_DATE_PAGED,
                    ps -> {
                        ps.setTimestamp(1, new Timestamp(endDate.getTime()));
                    },
                    ps -> {
                        ps.setTimestamp(1, new Timestamp(endDate.getTime()));
                        ps.setLong(2, offset);
                        ps.setLong(3, length);
                    });
        });
    }

    @Override
    public Page<Article> listByTagBeforeDate(Tag tag, Date endDate, long offset, long length) {
        return withReadonlyConnection(dataSource, con -> {
            return loadArticlesPaged(
                    con, offset, length, COUNT_BY_TAG_BEFORE_DATE, LIST_BY_TAG_BEFORE_DATE_PAGED,
                    ps -> {
                        ps.setLong(1, tag.getId());
                        ps.setTimestamp(2, new Timestamp(endDate.getTime()));
                    },
                    ps -> {
                        ps.setLong(1, tag.getId());
                        ps.setTimestamp(2, new Timestamp(endDate.getTime()));
                        ps.setLong(3, offset);
                        ps.setLong(4, length);
                    });
        });
    }

    @Override
    public List<Article> listBetweenDates(Date startDate, Date endDate) {
        return withReadonlyConnection(dataSource, con -> {
            return loadArticles(con, LIST_BETWEEN_DATES, ps -> {
                ps.setTimestamp(1, new Timestamp(startDate.getTime()));
                ps.setTimestamp(2, new Timestamp(endDate.getTime()));
            });
        });
    }

    @Override
    public List<Article> listByTagBetweenDates(Tag tag, Date startDate, Date endDate) {
        return withReadonlyConnection(dataSource, con -> {
            return loadArticles(con, LIST_BY_TAG_BETWEEN_DATES, ps -> {
                ps.setLong(1, tag.getId());
                ps.setTimestamp(2, new Timestamp(startDate.getTime()));
                ps.setTimestamp(3, new Timestamp(endDate.getTime()));
            });
        });
    }

    private Long createArticle(Connection con, Article article) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(INSERT)) {
            addSaveParams(ps, article);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Unable to save article");
                }
                article.setId(rs.getLong(1));
            }
        }
        populateTagLink(con, article);
        return article.getId();
    }

    private Long updateArticle(Connection con, Article article) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(UPDATE)) {
            addSaveParams(ps, article);
            ps.setLong(11, article.getId());
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new DataAccessException("Unable to save article");
            }
        }
        deleteTagLink(con, article);
        populateTagLink(con, article);
        return article.getId();
    }

    private void addSaveParams(PreparedStatement ps, Article article) throws SQLException {
        ps.setString(1, article.getContentType().name());
        User createdBy = article.getCreatedByUser();
        if (createdBy == null) {
            ps.setNull(2, Types.BIGINT);
        } else {
            ps.setLong(2, createdBy.getId());
        }
        ps.setTimestamp(3, new Timestamp(article.getCreationDate().getTime()));
        User modifiedBy = article.getModifiedByUser();
        if (modifiedBy == null) {
            ps.setNull(4, Types.BIGINT);
        } else {
            ps.setLong(4, modifiedBy.getId());
        }
        Date modified = article.getModificationDate();
        if (modified == null) {
            ps.setNull(5, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(5, new Timestamp(modified.getTime()));
        }
        ps.setString(6, article.getTitle());
        ps.setString(7, article.getPermalink());
        ps.setString(8, article.getContent());
        ps.setString(9, article.getSummary());
        ps.setBoolean(10, article.isCommentsEnabled());
    }

    private Page<Article> loadArticlesPaged(
            Connection con,
            long offset,
            long length,
            String countSql,
            String querySql,
            UncheckedConsumer<PreparedStatement> countCallback,
            UncheckedConsumer<PreparedStatement> queryCallback) throws Exception {
        long count;
        try (PreparedStatement ps = con.prepareStatement(countSql)) {
            countCallback.invoke(ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Unable to retrieve articles");
                }
                count = rs.getLong(1);
            }
        }
        var articles = loadArticles(con, querySql, queryCallback);
        return new Page<>(articles, offset, count, length);
    }

    private List<Article> loadArticles(
            Connection con,
            String sql,
            UncheckedConsumer<PreparedStatement> callback) throws Exception {
        List<Article> articles = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            callback.invoke(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    articles.add(populateArticle(rs));
                }
            }
        }
        Map<Long, Article> map = new HashMap<>();
        for (Article article : articles) {
            map.put(article.getId(), article);
        }
        populateTags(con, map);
        populateComments(con, map);
        return articles;
    }

    private Article loadArticle(
            Connection con,
            String sql,
            UncheckedConsumer<PreparedStatement> callback) throws Exception {

        Article article;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            callback.invoke(ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                article = populateArticle(rs);
            }
        }
        var map = Map.of(article.getId(), article);
        populateTags(con, map);
        populateComments(con, map);
        return article;
    }

    private void populateComments(Connection con, Map<Long, Article> articles) throws SQLException {
        Object[] keys = articles.keySet().stream().toArray();
        try (PreparedStatement ps = con.prepareStatement(SELECT_COMMENTS_BY_ARTICLE_IDS)) {
            ps.setArray(1, con.createArrayOf(JDBCType.BIGINT.name(), keys));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long articleId = rs.getLong(COL_ARTICLE_ID);
                    Article article = articles.get(articleId);
                    if (article != null) {
                        Comment comment = new Comment();
                        comment.setId(rs.getLong(COL_COMMENT_ID));
                        comment.setArticle(article);
                        comment.setContentType(ContentType.valueOf(rs.getString(COL_COMMENT_CONTENT_TYPE)));
                        long createUserId = rs.getLong(COL_COMMENT_CREATE_USER_ID);
                        if (!rs.wasNull()) {
                            User user = new User();
                            user.setId(createUserId);
                            user.setUserName(rs.getString(COL_COMMENT_CREATE_USERNAME));
                            user.setEmailAddress(rs.getString(COL_COMMENT_CREATE_EMAIL));
                            user.setWebsite(rs.getString(COL_COMMENT_CREATE_WEBSITE));
                            comment.setCreatedByUser(user);
                        }
                        comment.setCreationDate(rs.getTimestamp(COL_COMMENT_CREATE_DATE));
                        comment.setAnonymousUserName(rs.getString(COL_COMMENT_ANON_USER_NAME));
                        comment.setAnonymousEmailAddress(rs.getString(COL_COMMENT_ANON_EMAIL_ADDRESS));
                        comment.setAnonymousWebsite(rs.getString(COL_COMMENT_ANON_WEBSITE));
                        comment.setTitle(rs.getString(COL_COMMENT_TITLE));
                        comment.setContent(rs.getString(COL_COMMENT_CONTENT));
                        comment.setVisible(rs.getBoolean(COL_COMMENT_VISIBLE));
                        comment.setModerationStatus(ModerationStatus.valueOf(rs.getString(COL_COMMENT_MODERATION_STATUS)));
                        comment.setIpAddress(rs.getString(COL_COMMENT_IP_ADDRESS));
                        comment.setReferrer(rs.getString(COL_COMMENT_REFERRER));
                        comment.setUserAgent(rs.getString(COL_COMMENT_USER_AGENT));
                        article.getComments().add(comment);
                    }
                }
            }
        }
    }

    private void populateTags(Connection con, Map<Long, Article> articles) throws SQLException {
        Object[] keys = articles.keySet().stream().toArray();
        try (PreparedStatement ps = con.prepareStatement(SELECT_TAGS_BY_ARTICLE_IDS)) {
            ps.setArray(1, con.createArrayOf(JDBCType.BIGINT.name(), keys));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long articleId = rs.getLong(COL_ARTICLE_ID);
                    Article article = articles.get(articleId);
                    if (article != null) {
                        Tag tag = new Tag();
                        tag.setId(rs.getLong(COL_TAG_ID));
                        tag.setName(rs.getString(COL_TAG_NAME));
                        tag.setDisplayName(rs.getString(COL_TAG_DISPLAY_NAME));
                        article.getTags().add(tag);
                    }
                }
            }
        }
    }

    private Article populateArticle(ResultSet rs) throws SQLException {
        Article article = new Article();
        article.setId(rs.getLong(COL_ARTICLE_ID));
        article.setContentType(ContentType.valueOf(rs.getString(COL_CONTENT_TYPE)));
        article.setModificationDate(rs.getTimestamp(COL_MODIFY_DATE));
        article.setTitle(rs.getString(COL_TITLE));
        article.setPermalink(rs.getString(COL_PERMALINK));
        article.setContent(rs.getString(COL_CONTENT));
        article.setSummary(rs.getString(COL_SUMMARY));
        article.setCommentsEnabled(rs.getBoolean(COL_COMMENTS_ENABLED));

        long createUserId = rs.getLong(COL_CREATE_USER_ID);
        if (!rs.wasNull()) {
            User user = new User();
            user.setId(createUserId);
            user.setUserName(rs.getString(COL_CREATE_USERNAME));
            user.setEmailAddress(rs.getString(COL_CREATE_EMAIL));
            user.setWebsite(rs.getString(COL_CREATE_WEBSITE));
            article.setCreatedByUser(user);
        }
        article.setCreationDate(rs.getTimestamp(COL_CREATE_DATE));

        long modifyUserId = rs.getLong(COL_MODIFY_USER_ID);
        if (!rs.wasNull()) {
            User user = new User();
            user.setId(modifyUserId);
            user.setUserName(rs.getString(COL_MODIFY_USERNAME));
            user.setEmailAddress(rs.getString(COL_MODIFY_EMAIL));
            user.setWebsite(rs.getString(COL_MODIFY_WEBSITE));
            article.setModifiedByUser(user);
        }
        return article;
    }

    private void deleteTagLink(Connection con, Article article) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(DELETE_TAG_LINK_BY_ARTICLE_ID)) {
            ps.setLong(1, article.getId());
            ps.executeUpdate();
        }
    }

    private void populateTagLink(Connection con, Article article) throws SQLException {
        if (article.getTags().isEmpty()) {
            return;
        }
        try (PreparedStatement ps = con.prepareStatement(INSERT_TAG_LINK)) {
            long articleId = article.getId();
            for (Tag tag : article.getTags()) {
                ps.setLong(1, articleId);
                ps.setLong(2, tag.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

}
