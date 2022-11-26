package org.randomcoder.dao;

import jakarta.inject.Inject;
import org.randomcoder.article.moderation.ModerationStatus;
import org.randomcoder.content.ContentType;
import org.randomcoder.db.Comment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component("commentDao")
public class CommentDaoImpl implements CommentDao {

    private DataSource dataSource;

    @Inject
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String SELECT_ALL = """
            SELECT
                comment_id, article_id, content_type, create_user_id, create_date,
                anonymous_user_name, anonymous_email_address, anonymous_website,
                title, content, visible, moderation_status, referrer, ip_address, user_agent
            FROM comments""";

    private static final String FIND_BY_ID = SELECT_ALL + " WHERE comment_id = ?";
    private static final String LIST_FOR_MODERATION_PAGED =
            SELECT_ALL + " WHERE moderation_status = 'PENDING' ORDER BY comment_id OFFSET ? LIMIT ?";

    private static final String COL_COMMENT_ID = "comment_id";
    private static final String COL_ARTICLE_ID = "article_id";
    private static final String COL_CONTENT_TYPE = "content_type";
    private static final String COL_CREATE_USER_ID = "create_user_id";
    private static final String COL_CREATE_DATE = "create_date";
    private static final String COL_ANON_USER_NAME = "anonymous_user_name";
    private static final String COL_ANON_EMAIL_ADDR = "anonymous_email_address";
    private static final String COL_ANON_WEBSITE = "anonymous_website";
    private static final String COL_TITLE = "title";
    private static final String COL_CONTENT = "content";
    private static final String COL_VISIBLE = "visible";
    private static final String COL_MODERATION_STATUS = "moderation_status";
    private static final String COL_REFERRER = "referrer";
    private static final String COL_IP_ADDRESS = "ip_address";
    private static final String COL_USER_AGENT = "user_agent";

    @Override
    public Page<Comment> listForModeration(long offset, long length) {
        return null;
    }

    private Comment loadComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong(COL_COMMENT_ID));
        //comment.setArticle();
        comment.setContentType(ContentType.valueOf(rs.getString(COL_CONTENT_TYPE)));
        //comment.setCreatedByUser();
        comment.setCreationDate(rs.getTimestamp(COL_CREATE_DATE));
        comment.setAnonymousUserName(rs.getString(COL_ANON_USER_NAME));
        comment.setAnonymousEmailAddress(rs.getString(COL_ANON_EMAIL_ADDR));
        comment.setAnonymousWebsite(rs.getString(COL_ANON_WEBSITE));
        comment.setTitle(rs.getString(COL_TITLE));
        comment.setContent(rs.getString(COL_CONTENT));
        comment.setVisible(rs.getBoolean(COL_VISIBLE));
        comment.setModerationStatus(ModerationStatus.valueOf(rs.getString(COL_MODERATION_STATUS)));
        comment.setReferrer(rs.getString(COL_REFERRER));
        comment.setIpAddress(rs.getString(COL_IP_ADDRESS));
        comment.setUserAgent(rs.getString(COL_USER_AGENT));
        return comment;
    }

}
