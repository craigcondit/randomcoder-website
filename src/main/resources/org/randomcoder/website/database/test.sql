-- Add admin user, password 'admin'
INSERT INTO users (username, password, email, website, enabled) VALUES (
	'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', 'admin@randomcoder.org', 'https://randomcoder.org/', true
);

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

-- Add test user, password 'test'
INSERT INTO users (username, password, email, website, enabled) VALUES (
	'test', 'a94a8fe5ccb19ba61c4c0873d391e987982fbbd3', 'test@randomcoder.org', null, true
);

INSERT INTO user_role_link (user_id, role_id) VALUES (
	(SELECT user_id FROM users WHERE username = 'test'),
	(SELECT role_id FROM roles WHERE name = 'ROLE_POST_ARTICLES')
);
