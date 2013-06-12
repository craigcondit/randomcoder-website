-- Change role names to support spring security naming conventions
UPDATE roles SET name = 'ROLE_MANAGE_USERS' WHERE name = 'manage-users';
UPDATE roles SET name = 'ROLE_POST_ARTICLES' WHERE name = 'article-post';
UPDATE roles SET name = 'ROLE_MANAGE_ARTICLES' WHERE name = 'article-admin';
UPDATE roles SET name = 'ROLE_DEVELOPMENT_DWR' WHERE name = 'development-dwr';
UPDATE roles SET name = 'ROLE_MANAGE_TAGS' WHERE name = 'manage-tags';
UPDATE roles SET name = 'ROLE_MANAGE_COMMENTS' WHERE name = 'manage-comments';

-- Make password nullable to support cardspace-only logins
ALTER TABLE users ALTER COLUMN password DROP NOT NULL;
ALTER TABLE users ADD COLUMN login_date TIMESTAMP WITH TIME ZONE NULL;
ALTER TABLE users ADD COLUMN website VARCHAR(255) NULL;

-- cardspace_tokens
CREATE SEQUENCE cardspace_tokens_seq;

CREATE TABLE cardspace_tokens (
	cardspace_token_id BIGINT NOT NULL DEFAULT NEXTVAL('cardspace_tokens_seq'),
	user_id BIGINT NOT NULL,
	ppid VARCHAR(1024) NOT NULL,
	issuer_hash VARCHAR(40) NOT NULL,
	email_address VARCHAR(320) NOT NULL,
	create_date TIMESTAMP WITH TIME ZONE NOT NULL,
	login_date TIMESTAMP WITH TIME ZONE NULL,
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
CREATE SEQUENCE cardspace_seen_tokens_seq;

CREATE TABLE cardspace_seen_tokens (
	cardspace_seen_token_id BIGINT NOT NULL DEFAULT NEXTVAL('cardspace_seen_tokens_seq'),	
	assertion_id VARCHAR(1024) NOT NULL,
	ppid VARCHAR(1024) NOT NULL,
	issuer_hash VARCHAR(40) NOT NULL,
	create_date TIMESTAMP WITH TIME ZONE NOT NULL,
	CONSTRAINT cardspace_seen_tokens_pkey PRIMARY KEY (cardspace_seen_token_id),
	CONSTRAINT cardspace_seen_tokens_key UNIQUE (assertion_id, ppid, issuer_hash),
	CONSTRAINT cardspace_seen_tokens_assertion_id_ck CHECK (assertion_id <> ''),
	CONSTRAINT cardspace_seen_tokens_ppid_ck CHECK (ppid <> ''),
	CONSTRAINT cardspace_seen_tokens_issuer_hash_ck CHECK (issuer_hash <> '')
);
CREATE INDEX cardspace_seen_tokens_create_date_key ON cardspace_seen_tokens (create_date);

