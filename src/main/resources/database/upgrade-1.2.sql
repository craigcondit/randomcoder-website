-- comments
CREATE SEQUENCE comments_seq;

CREATE TABLE comments (
	comment_id BIGINT NOT NULL DEFAULT NEXTVAL('comments_seq'),
	article_id BIGINT NOT NULL,
	content_type VARCHAR(255) NOT NULL,
	create_user_id BIGINT NULL,
	create_date TIMESTAMP WITH TIME ZONE NOT NULL,
	anonymous_user_name VARCHAR(30) NULL,
	anonymous_email_address VARCHAR(320) NULL,
	anonymous_website VARCHAR(255) NULL,
	title VARCHAR(255) NOT NULL,
	content TEXT NOT NULL,	
	CONSTRAINT comments_pkey PRIMARY KEY (comment_id),
	CONSTRAINT comments_content_type_ck CHECK (content_type IN ('TEXT', 'XHTML')),
	CONSTRAINT comments_article_id_fk
		FOREIGN KEY (article_id) REFERENCES articles (article_id)
		ON DELETE SET NULL ON UPDATE SET NULL,
	CONSTRAINT comments_create_user_id_fk
		FOREIGN KEY (create_user_id) REFERENCES users (user_id)
		ON DELETE SET NULL ON UPDATE SET NULL,
	CONSTRAINT comments_title_ck CHECK (title <> '')
);

INSERT INTO roles (name, description) VALUES ('manage-comments', 'Manage comments');