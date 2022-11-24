package org.randomcoder.dao;

import org.randomcoder.db.CommentIp;
import org.randomcoder.db.CommentReferrer;
import org.randomcoder.db.CommentUserAgent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CommentDao {

    private static final String QUERY_UA_FIND_BY_NAME = """
            SELECT comment_user_agent_id, user_agent, create_date
            FROM comment_useragents
            WHERE user_agent = ?""";

    private static final String QUERY_UA_INSERT = """
            INSERT INTO comment_useragents (
                comment_user_agent_id, user_agent, create_date)
            SELECT nextval('comment_useragents_seq'), ?, ?
            RETURNING comment_user_agent_id""";

    private static final String QUERY_UA_UPDATE = """
            UPDATE comment_useragents
            SET user_agent = ?
            WHERE comment_user_agent_id = ?""";

    private static final String QUERY_REF_FIND_BY_NAME = """
            SELECT comment_referrer_id, referrer, create_date
            FROM comment_referrers
            WHERE referrer = ?""";

    private static final String QUERY_REF_INSERT = """
            INSERT INTO comment_referrers (
                comment_referrer_id, referrer, create_date)
            SELECT nextval('comment_referrers_seq'), ?, ?
            RETURNING comment_referrer_id""";

    private static final String QUERY_REF_UPDATE = """
            UPDATE comment_referrers
            SET referrer = ?
            WHERE comment_referrer_id = ?""";

    private static final String QUERY_IP_FIND_BY_NAME = """
            SELECT comment_ip_id, ip_address, create_date
            FROM comment_ips
            WHERE ip_address = ?""";

    private static final String QUERY_IP_INSERT = """
            INSERT INTO comment_ips (
                comment_ip_id, ip_address, create_date)
            SELECT nextval('comment_ips_seq'), ?, ?
            RETURNING comment_ip_id""";

    private static final String QUERY_IP_UPDATE = """
            UPDATE comment_referrers
            SET referrer = ?
            WHERE comment_referrer_id = ?""";

    private static final String COL_COMMENT_ID = "comment_id";
    private static final String COL_COMMENT_ARTICLE_ID = "article_id";
    private static final String COL_COMMENT_CONTENT_TYPE = "content_type";
    private static final String COL_COMMENT_CREATE_USER_ID = "create_user_id";
    private static final String COL_COMMENT_CREATE_DATE = "create_date";
    private static final String COL_COMMENT_ANON_USER_NAME = "anonymous_user_name";
    private static final String COL_COMMENT_ANON_EMAIL_ADDR = "anonymous_email_address";
    private static final String COL_COMMENT_ANON_WEBSITE = "anonymous_website";
    private static final String COL_COMMENT_TITLE = "title";
    private static final String COL_COMMENT_CONTENT = "content";
    private static final String COL_COMMENT_VISIBLE = "visible";
    private static final String COL_COMMENT_MODERATION_STATUS = "moderation_status";
    private static final String COL_COMMENT_REF_ID = "comment_referrer_id";
    private static final String COL_COMMENT_IP_ID = "comment_ip_id";
    private static final String COL_COMMENT_UA_ID = "comment_useragent_id";

    private static final String COL_UA_ID = "comment_user_agent_id";
    private static final String COL_UA_USER_AGENT = "user_agent";
    private static final String COL_UA_CREATE_DATE = "create_date";

    private static final String COL_REF_ID = "comment_referrer_id";
    private static final String COL_REF_REFERRER = "referrer";
    private static final String COL_REF_CREATE_DATE = "create_date";

    private static final String COL_IP_ID = "comment_ip_id";
    private static final String COL_IP_ADDRESS = "ip_address";
    private static final String COL_IP_CREATE_DATE = "create_date";

    public CommentUserAgent findUserAgentByName(Connection con, String name) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(QUERY_UA_FIND_BY_NAME)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return loadUserAgent(rs);
                } else {
                    return null;
                }
            }
        }
    }

    public CommentReferrer findReferrerByName(Connection con, String name) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(QUERY_REF_FIND_BY_NAME)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return loadReferrer(rs);
                } else {
                    return null;
                }
            }
        }
    }

    public CommentIp findIpByName(Connection con, String name) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(QUERY_IP_FIND_BY_NAME)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return loadIp(rs);
                } else {
                    return null;
                }
            }
        }
    }

    public Long saveUserAgent(Connection con, CommentUserAgent object) throws SQLException {
        return object.getId() == null
                ? insertUserAgent(con, object)
                : updateUserAgent(con, object);
    }

    public Long saveReferrer(Connection con, CommentReferrer object) throws SQLException {
        return object.getId() == null
                ? insertReferrer(con, object)
                : updateReferrer(con, object);
    }

    public Long saveIp(Connection con, CommentIp object) throws SQLException {
        return object.getId() == null
                ? insertIp(con, object)
                : updateIp(con, object);
    }

    private Long insertUserAgent(Connection con, CommentUserAgent object) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(QUERY_UA_INSERT)) {
            ps.setString(1, object.getUserAgentName());
            ps.setTimestamp(2, new Timestamp(object.getCreationDate().getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Unable to retrieve primary key");
                }
                Long id = rs.getLong(1);
                object.setId(id);
                return id;
            }
        }
    }

    private Long insertReferrer(Connection con, CommentReferrer object) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(QUERY_REF_INSERT)) {
            ps.setString(1, object.getReferrerUri());
            ps.setTimestamp(2, new Timestamp(object.getCreationDate().getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Unable to retrieve primary key");
                }
                Long id = rs.getLong(1);
                object.setId(id);
                return id;
            }
        }
    }

    private Long insertIp(Connection con, CommentIp object) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(QUERY_IP_INSERT)) {
            ps.setString(1, object.getIpAddress());
            ps.setTimestamp(2, new Timestamp(object.getCreationDate().getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Unable to retrieve primary key");
                }
                Long id = rs.getLong(1);
                object.setId(id);
                return id;
            }
        }
    }

    private Long updateUserAgent(Connection con, CommentUserAgent object) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(QUERY_UA_UPDATE)) {
            ps.setString(1, object.getUserAgentName());
            ps.setLong(2, object.getId());
            ps.executeUpdate();
        }
        return object.getId();
    }

    private Long updateReferrer(Connection con, CommentReferrer object) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(QUERY_REF_UPDATE)) {
            ps.setString(1, object.getReferrerUri());
            ps.setLong(2, object.getId());
            ps.executeUpdate();
        }
        return object.getId();
    }

    private Long updateIp(Connection con, CommentIp object) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(QUERY_IP_UPDATE)) {
            ps.setString(1, object.getIpAddress());
            ps.setLong(2, object.getId());
            ps.executeUpdate();
        }
        return object.getId();
    }

    private CommentUserAgent loadUserAgent(ResultSet rs) throws SQLException {
        var result = new CommentUserAgent();
        result.setId(rs.getLong(COL_UA_ID));
        result.setUserAgentName(rs.getString(COL_UA_USER_AGENT));
        result.setCreationDate(rs.getTimestamp(COL_UA_CREATE_DATE));
        return result;
    }

    private CommentReferrer loadReferrer(ResultSet rs) throws SQLException {
        var result = new CommentReferrer();
        result.setId(rs.getLong(COL_REF_ID));
        result.setReferrerUri(rs.getString(COL_REF_REFERRER));
        result.setCreationDate(rs.getTimestamp(COL_REF_CREATE_DATE));
        return result;
    }

    private CommentIp loadIp(ResultSet rs) throws SQLException {
        var result = new CommentIp();
        result.setId(rs.getLong(COL_IP_ID));
        result.setIpAddress(rs.getString(COL_IP_ADDRESS));
        result.setCreationDate(rs.getTimestamp(COL_IP_CREATE_DATE));
        return result;
    }

}
