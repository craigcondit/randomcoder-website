-- Add admin user, password 'admin'
INSERT INTO users (username, password, email, enabled) VALUES (
	'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', 'admin@randomcoder.com', true
);


UPDATE roles SET name = 'ROLE_MANAGE_USERS' WHERE name = 'manage-users';
UPDATE roles SET name = 'ROLE_POST_ARTICLES' WHERE name = 'article-post';
UPDATE roles SET name = 'ROLE_MANAGE_ARTICLES' WHERE name = 'article-admin';
UPDATE roles SET name = 'ROLE_DEVELOPMENT_DWR' WHERE name = 'development-dwr';
UPDATE roles SET name = 'ROLE_MANAGE_TAGS' WHERE name = 'manage-tags';
UPDATE roles SET name = 'ROLE_MANAGE_COMMENTS' WHERE name = 'manage-comments';


INSERT INTO user_role_link (user_id, role_id) VALUES (
	(SELECT user_id FROM users WHERE username = 'admin'),
	(SELECT role_id FROM roles WHERE name = 'ROLE_MANAGE_USERS')
);

INSERT INTO user_role_link (user_id, role_id) VALUES (
	(SELECT user_id FROM users WHERE username = 'admin'),
	(SELECT role_id FROM roles WHERE name = 'ROLE_MANAGE_ARTICLES')
);

INSERT INTO user_role_link (user_id, role_id) VALUES (
	(SELECT user_id FROM users WHERE username = 'admin'),
	(SELECT role_id FROM roles WHERE name = 'ROLE_MANAGE_TAGS')
);

INSERT INTO user_role_link (user_id, role_id) VALUES (
	(SELECT user_id FROM users WHERE username = 'admin'),
	(SELECT role_id FROM roles WHERE name = 'ROLE_MANAGE_COMMENTS')
);

INSERT INTO user_role_link (user_id, role_id) VALUES (
	(SELECT user_id FROM users WHERE username = 'admin'),
	(SELECT role_id FROM roles WHERE name = 'ROLE_DEVELOPMENT_DWR')
);

-- Add test user, password 'test'
INSERT INTO users (username, password, email, enabled) VALUES (
	'test', 'a94a8fe5ccb19ba61c4c0873d391e987982fbbd3', 'test@randomcoder.com', true
);

INSERT INTO user_role_link (user_id, role_id) VALUES (
	(SELECT user_id FROM users WHERE username = 'test'),
	(SELECT role_id FROM roles WHERE name = 'ROLE_POST_ARTICLES')
);
