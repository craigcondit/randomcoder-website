-- 1.0
GRANT SELECT, UPDATE ON users_seq TO randomcoder;
GRANT SELECT, INSERT, UPDATE, DELETE ON users TO randomcoder;
GRANT SELECT, UPDATE ON roles_seq TO randomcoder;
GRANT SELECT, INSERT, UPDATE, DELETE ON roles TO randomcoder;
GRANT SELECT, INSERT, UPDATE, DELETE ON user_role_link TO randomcoder;
GRANT SELECT, UPDATE ON articles_seq TO randomcoder;
GRANT SELECT, INSERT, UPDATE, DELETE ON articles TO randomcoder;
GRANT SELECT, UPDATE ON tags_seq TO randomcoder;
GRANT SELECT, INSERT, UPDATE, DELETE ON tags TO randomcoder;
GRANT SELECT, INSERT, UPDATE, DELETE ON article_tag_link TO randomcoder;

-- 1.2
GRANT SELECT, UPDATE ON comments_seq TO randomcoder;
GRANT SELECT, INSERT, UPDATE, DELETE ON comments TO randomcoder;

-- 2.0
GRANT SELECT, UPDATE ON cardspace_tokens_seq TO randomcoder;
GRANT SELECT, INSERT, UPDATE, DELETE ON cardspace_tokens TO randomcoder;

GRANT SELECT, UPDATE ON cardspace_seen_tokens_seq TO randomcoder;
GRANT SELECT, INSERT, UPDATE, DELETE ON cardspace_seen_tokens TO randomcoder;
