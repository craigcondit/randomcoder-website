-- comment_referrers
CREATE SEQUENCE comment_referrers_seq;

CREATE TABLE comment_referrers (
	comment_referrer_id BIGINT NOT NULL DEFAULT NEXTVAL('comment_referrers_seq'),
	referrer VARCHAR(1024) NOT NULL,
	create_date TIMESTAMP WITH TIME ZONE NOT NULL,
	CONSTRAINT comment_referrers_pkey PRIMARY KEY (comment_referrer_id),
	CONSTRAINT comment_referrers_key UNIQUE (referrer),
	CONSTRAINT comment_referrers_referrer_ck CHECK (referrer <> '')
);
CREATE INDEX comment_referrers_create_date_idx ON comment_referrers (create_date);

-- comment_ips
CREATE SEQUENCE comment_ips_seq;

CREATE TABLE comment_ips (
	comment_ip_id BIGINT NOT NULL DEFAULT NEXTVAL('comment_ips_seq'),
	ip_address varchar(255) NOT NULL,
	create_date TIMESTAMP WITH TIME ZONE NOT NULL,
	CONSTRAINT comment_ips_pkey PRIMARY KEY (comment_ip_id),
	CONSTRAINT comment_ips_key UNIQUE (ip_address),
	CONSTRAINT comment_ips_ip_address_ck CHECK (ip_address <> '')
);
CREATE INDEX comment_ips_create_date_idx ON comment_ips (create_date);

-- comment_useragents
CREATE SEQUENCE comment_useragents_seq;

CREATE TABLE comment_useragents (
	comment_useragent_id BIGINT NOT NULL DEFAULT NEXTVAL('comment_useragents_seq'),
	user_agent varchar(255) NOT NULL,
	create_date TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT comment_useragents_pkey PRIMARY KEY (comment_useragent_id),
  CONSTRAINT comment_useragents_key UNIQUE (user_agent),
	CONSTRAINT comment_useragents_user_agent_ck CHECK (user_agent <> '')
);
CREATE INDEX comment_useragents_create_date_idx ON comment_useragents (create_date);

-- modify comments table to track visibility
ALTER TABLE comments ADD COLUMN visible BOOLEAN NULL;
UPDATE comments SET visible = true;
ALTER TABLE comments ALTER COLUMN visible SET NOT NULL;
CREATE INDEX comments_visible_idx ON comments (visible);

-- modify comments table to track moderation status
ALTER TABLE comments ADD COLUMN moderation_status VARCHAR(255) NULL;
UPDATE comments SET moderation_status = 'PENDING';
ALTER TABLE comments ALTER COLUMN moderation_status SET NOT NULL;
ALTER TABLE comments ADD CONSTRAINT comments_moderation_status_ck CHECK (moderation_status <> '');
CREATE INDEX comments_moderation_status_idx ON comments (moderation_status);

-- modify comments table to track referrers
ALTER TABLE comments ADD COLUMN comment_referrer_id BIGINT NULL;
ALTER TABLE comments ADD CONSTRAINT comments_comment_referrer_id_fk
	FOREIGN KEY (comment_referrer_id) REFERENCES comment_referrers (comment_referrer_id)
	ON DELETE SET NULL ON UPDATE SET NULL;
CREATE INDEX comments_comment_referrer_id_idx ON comments (comment_referrer_id);

-- modify comments table to track ips
ALTER TABLE comments ADD COLUMN comment_ip_id BIGINT NULL;
ALTER TABLE comments ADD CONSTRAINT comments_comment_ip_id_fk
	FOREIGN KEY (comment_ip_id) REFERENCES comment_ips (comment_ip_id)
	ON DELETE SET NULL ON UPDATE SET NULL;
CREATE INDEX comments_comment_ip_id_key ON comments (comment_ip_id);

-- modify comments table to track user agents
ALTER TABLE comments ADD COLUMN comment_useragent_id BIGINT NULL;
ALTER TABLE comments ADD CONSTRAINT comments_comment_useragent_id_fk
	FOREIGN KEY (comment_useragent_id) REFERENCES comment_useragents (comment_useragent_id)
	ON DELETE SET NULL ON UPDATE SET NULL;
CREATE INDEX comments_comment_useragent_id_idx ON comments (comment_useragent_id);
