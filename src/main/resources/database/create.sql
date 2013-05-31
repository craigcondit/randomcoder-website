-- users
CREATE SEQUENCE users_seq;
CREATE TABLE users (
	user_id BIGINT NOT NULL DEFAULT NEXTVAL('users_seq'),
	username VARCHAR(30) NOT NULL,
	password VARCHAR(255) NOT NULL,
	email VARCHAR(320) NOT NULL,
	enabled BOOLEAN NOT NULL,
	CONSTRAINT users_pkey PRIMARY KEY (user_id),
	CONSTRAINT users_username_key UNIQUE (username),
	CONSTRAINT users_username_ck CHECK (username <> '')
);
CREATE INDEX users_enabled_idx ON users (enabled);

-- roles
CREATE SEQUENCE roles_seq;
CREATE TABLE roles (
	role_id BIGINT NOT NULL DEFAULT NEXTVAL('roles_seq'),
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
CREATE SEQUENCE articles_seq;
CREATE TABLE articles (
	article_id BIGINT NOT NULL DEFAULT NEXTVAL('articles_seq'),
	content_type VARCHAR(255) NOT NULL,
	create_user_id BIGINT NULL,
	create_date TIMESTAMP WITH TIME ZONE NOT NULL,
	modify_user_id BIGINT NULL,
	modify_date TIMESTAMP WITH TIME ZONE NULL,
	title VARCHAR(255) NOT NULL,
	permalink VARCHAR(100) NULL,
	content TEXT NOT NULL,
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
CREATE SEQUENCE tags_seq;
CREATE TABLE tags (
	tag_id BIGINT NOT NULl DEFAULT NEXTVAL('tags_seq'),
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
