ALTER TABLE comments ADD COLUMN ip_address VARCHAR(255);
ALTER TABLE comments ADD COLUMN referrer VARCHAR(1024);
ALTER TABLE comments ADD COLUMN user_agent VARCHAR(255);

UPDATE comments c SET ip_address = (
    SELECT ip_address
    FROM comment_ips i
    WHERE i.comment_ip_id = c.comment_ip_id);

UPDATE comments c SET referrer = (
    SELECT referrer
    FROM comment_referrers r
    WHERE r.comment_referrer_id = c.comment_referrer_id);

UPDATE comments c SET user_agent = (
    SELECT user_agent
    FROM comment_useragents u
    WHERE u.comment_useragent_id = c.comment_useragent_id);

ALTER TABLE comments DROP COLUMN comment_ip_id;
ALTER TABLE comments DROP COLUMN comment_referrer_id;
ALTER TABLE comments DROP COLUMN comment_useragent_id;

DROP TABLE comment_ips;
DROP SEQUENCE comment_ips_seq;
DROP TABLE comment_referrers;
DROP SEQUENCE comment_referrers_seq;
DROP TABLE comment_useragents;
DROP SEQUENCE comment_useragents_seq;

ALTER TABLE comments
DROP CONSTRAINT comments_article_id_fk;

ALTER TABLE comments
ADD CONSTRAINT comments_article_id_fk
FOREIGN KEY (article_id)
REFERENCES articles(article_id)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE comments
ALTER COLUMN article_id SET NOT NULL;

