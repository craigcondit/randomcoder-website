-- drop existing data
DROP TABLE IF EXISTS cardspace_seen_tokens;
DROP TABLE IF EXISTS cardspace_tokens;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS comment_useragents;
DROP TABLE IF EXISTS comment_ips;
DROP TABLE IF EXISTS comment_referrers;
DROP TABLE IF EXISTS article_tag_link;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS articles;
DROP TABLE IF EXISTS user_role_link;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

-- users
CREATE TABLE users (
	user_id BIGINT IDENTITY NOT NULL,
	username VARCHAR(30) NOT NULL,
	password VARCHAR(255) NULL,
	email VARCHAR(320) NOT NULL,
	enabled BOOLEAN NOT NULL,
	login_date TIMESTAMP NULL,
	website VARCHAR(255) NULL,
	CONSTRAINT users_pkey PRIMARY KEY (user_id),
	CONSTRAINT users_username_key UNIQUE (username),
	CONSTRAINT users_username_ck CHECK (username <> '')
);
CREATE INDEX users_enabled_idx ON users (enabled);

-- roles
CREATE TABLE roles (
	role_id BIGINT IDENTITY NOT NULL,
	name VARCHAR(30) NOT NULL,
	description VARCHAR(255),
	CONSTRAINT roles_pkey PRIMARY KEY (role_id),
	CONSTRAINT roles_name_key UNIQUE (name),
	CONSTRAINT roles_name_ck CHECK (name <> '')
);

-- user/role link
CREATE TABLE user_role_link (
	user_id BIGINT NOT NULL,
	role_id BIGINT NOT NULL,
	CONSTRAINT user_role_link_pkey PRIMARY KEY (user_id, role_id),
	CONSTRAINT user_role_link_user_id_fk
		FOREIGN KEY (user_id) REFERENCES users (user_id)
		ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT user_role_link_role_id_fk
		FOREIGN KEY (role_id) REFERENCES roles (role_id)
		ON DELETE CASCADE ON UPDATE CASCADE
);

-- articles
CREATE TABLE articles (
	article_id BIGINT IDENTITY NOT NULL,
	content_type VARCHAR(255) NOT NULL,
	create_user_id BIGINT NULL,
	create_date TIMESTAMP NOT NULL,
	modify_user_id BIGINT NULL,
	modify_date TIMESTAMP NULL,
	title VARCHAR(255) NOT NULL,
	permalink VARCHAR(100) NULL,
	content LONGVARCHAR NOT NULL,
	summary LONGVARCHAR NULL,
	CONSTRAINT articles_pkey PRIMARY KEY (article_id),
	CONSTRAINT articles_content_type_ck CHECK (content_type IN ('TEXT','XHTML')),
	CONSTRAINT articles_create_user_id_fk
		FOREIGN KEY (create_user_id) REFERENCES users (user_id)
		ON DELETE SET NULL ON UPDATE SET NULL,
	CONSTRAINT articles_modify_user_id_fk
		FOREIGN KEY (modify_user_id) REFERENCES users (user_id)
		ON DELETE SET NULL ON UPDATE SET NULL,
	CONSTRAINT articles_title_ck CHECK (title <> ''),
	CONSTRAINT articles_permalink_key UNIQUE (permalink)
);

-- tags
CREATE TABLE tags (
	tag_id BIGINT IDENTITY NOT NULL,
	name VARCHAR(255) NOT NULL,
	display_name VARCHAR(255) NOT NULL,
	CONSTRAINT tags_pkey PRIMARY KEY (tag_id),
	CONSTRAINT tags_name_key UNIQUE (name),
	CONSTRAINT tags_name_ck CHECK (name <> '')
);
CREATE INDEX tags_display_name_key ON tags (display_name);

-- article/tag link
CREATE TABLE article_tag_link (
	article_id BIGINT NOT NULL,
	tag_id BIGINT NOT NULL,
	CONSTRAINT article_tag_link_pkey PRIMARY KEY (article_id, tag_id),
	CONSTRAINT article_tag_link_article_id_fk
		FOREIGN KEY (article_id) REFERENCES articles (article_id)
		ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT article_tag_link_tag_id_fk
		FOREIGN KEY (tag_id) REFERENCES tags (tag_id)
		ON DELETE CASCADE ON UPDATE CASCADE
);

-- comment_referrers
CREATE TABLE comment_referrers (
	comment_referrer_id BIGINT IDENTITY NOT NULL,
	referrer VARCHAR(1024) NOT NULL,
	create_date TIMESTAMP NOT NULL,
	CONSTRAINT comment_referrers_pkey PRIMARY KEY (comment_referrer_id),
	CONSTRAINT comment_referrers_key UNIQUE (referrer),
	CONSTRAINT comment_referrers_referrer_ck CHECK (referrer <> '')
);
CREATE INDEX comment_referrers_create_date_idx ON comment_referrers (create_date);

-- comment_ips
CREATE TABLE comment_ips (
	comment_ip_id BIGINT IDENTITY NOT NULL,
	ip_address varchar(255) NOT NULL,
	create_date TIMESTAMP NOT NULL,
	CONSTRAINT comment_ips_pkey PRIMARY KEY (comment_ip_id),
	CONSTRAINT comment_ips_key UNIQUE (ip_address),
	CONSTRAINT comment_ips_ip_address_ck CHECK (ip_address <> '')
);
CREATE INDEX comment_ips_create_date_idx ON comment_ips (create_date);

-- comment_useragents
CREATE TABLE comment_useragents (
	comment_useragent_id BIGINT IDENTITY NOT NULL,
	user_agent varchar(255) NOT NULL,
	create_date TIMESTAMP NOT NULL,
  CONSTRAINT comment_useragents_pkey PRIMARY KEY (comment_useragent_id),
  CONSTRAINT comment_useragents_key UNIQUE (user_agent),
	CONSTRAINT comment_useragents_user_agent_ck CHECK (user_agent <> '')
);
CREATE INDEX comment_useragents_create_date_idx ON comment_useragents (create_date);

-- comments
CREATE TABLE comments (
	comment_id BIGINT IDENTITY NOT NULL,
	article_id BIGINT NOT NULL,
	content_type VARCHAR(255) NOT NULL,
	create_user_id BIGINT NULL,
	create_date TIMESTAMP NOT NULL,
	anonymous_user_name VARCHAR(30) NULL,
	anonymous_email_address VARCHAR(320) NULL,
	anonymous_website VARCHAR(255) NULL,
	title VARCHAR(255) NOT NULL,
	content LONGVARCHAR NOT NULL,	
	visible BOOLEAN NOT NULL,
	moderation_status VARCHAR(255) NOT NULL,
	comment_referrer_id BIGINT NULL,
	comment_ip_id BIGINT NULL,
	comment_useragent_id BIGINT NULL,
	CONSTRAINT comments_pkey PRIMARY KEY (comment_id),
	CONSTRAINT comments_content_type_ck CHECK (content_type IN ('TEXT', 'XHTML')),
	CONSTRAINT comments_article_id_fk
		FOREIGN KEY (article_id) REFERENCES articles (article_id)
		ON DELETE SET NULL ON UPDATE SET NULL,
	CONSTRAINT comments_create_user_id_fk
		FOREIGN KEY (create_user_id) REFERENCES users (user_id)
		ON DELETE SET NULL ON UPDATE SET NULL,
	CONSTRAINT comments_title_ck CHECK (title <> ''),
	CONSTRAINT comments_moderation_status_ck CHECK (moderation_status <> ''),
	CONSTRAINT comments_comment_referrer_id_fk
		FOREIGN KEY (comment_referrer_id) REFERENCES comment_referrers (comment_referrer_id)
		ON DELETE SET NULL ON UPDATE SET NULL,
	CONSTRAINT comments_comment_ip_id_fk
		FOREIGN KEY (comment_ip_id) REFERENCES comment_ips (comment_ip_id)
		ON DELETE SET NULL ON UPDATE SET NULL,
	CONSTRAINT comments_comment_useragent_id_fk
		FOREIGN KEY (comment_useragent_id) REFERENCES comment_useragents (comment_useragent_id)
		ON DELETE SET NULL ON UPDATE SET NULL		
);
CREATE INDEX comments_visible_idx ON comments (visible);
CREATE INDEX comments_moderation_status_idx ON comments (moderation_status);

-- cardspace_tokens
CREATE TABLE cardspace_tokens (
	cardspace_token_id BIGINT IDENTITY NOT NULL,
	user_id BIGINT NOT NULL,
	ppid VARCHAR(1024) NOT NULL,
	issuer_hash VARCHAR(40) NOT NULL,
	email_address VARCHAR(320) NOT NULL,
	create_date TIMESTAMP NOT NULL,
	login_date TIMESTAMP NULL,
	CONSTRAINT cardspace_tokens_pkey PRIMARY KEY (cardspace_token_id),
	CONSTRAINT cardspace_tokens_ppid_key UNIQUE (ppid),
	CONSTRAINT cardspace_tokens_user_id_fk
		FOREIGN KEY (user_id) REFERENCES users (user_id)
		ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT cardspace_tokens_ppid_ck CHECK (ppid <> ''),
	CONSTRAINT cardspace_tokens_email_address_ck CHECK (email_address <> ''),
	CONSTRAINT cardspace_tokens_issuer_hash_ck CHECK (issuer_hash <> '')
);

-- cardspace_seen_tokens
CREATE TABLE cardspace_seen_tokens (
	cardspace_seen_token_id BIGINT IDENTITY NOT NULL,	
	assertion_id VARCHAR(1024) NOT NULL,
	ppid VARCHAR(1024) NOT NULL,
	issuer_hash VARCHAR(40) NOT NULL,
	create_date TIMESTAMP NOT NULL,
	CONSTRAINT cardspace_seen_tokens_pkey PRIMARY KEY (cardspace_seen_token_id),
	CONSTRAINT cardspace_seen_tokens_key UNIQUE (assertion_id, ppid, issuer_hash),
	CONSTRAINT cardspace_seen_tokens_assertion_id_ck CHECK (assertion_id <> ''),
	CONSTRAINT cardspace_seen_tokens_ppid_ck CHECK (ppid <> ''),
	CONSTRAINT cardspace_seen_tokens_issuer_hash_ck CHECK (issuer_hash <> '')
);
CREATE INDEX cardspace_seen_tokens_create_date_key ON cardspace_seen_tokens (create_date);

-- Add security roles
INSERT INTO roles (name, description) VALUES ('ROLE_MANAGE_USERS', 'Manage users');
INSERT INTO roles (name, description) VALUES ('ROLE_POST_ARTICLES', 'Post articles');
INSERT INTO roles (name, description) VALUES ('ROLE_MANAGE_ARTICLES', 'Update other users'' articles');
INSERT INTO roles (name, description) VALUES ('ROLE_DEVELOPMENT_DWR', 'DWR Testing');
INSERT INTO roles (name, description) VALUES ('ROLE_MANAGE_TAGS', 'Manage tags');
INSERT INTO roles (name, description) VALUES ('ROLE_MANAGE_COMMENTS', 'Manage comments');

-- Shutdown HSQLDB
SHUTDOWN;