-- Add admin user, password 'admin'
INSERT INTO users (username, password, email, enabled) VALUES (
	'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', 'admin@randomcoder.com', true
);

INSERT INTO user_role_link (user_id, role_id) VALUES (
	(SELECT user_id FROM users WHERE username = 'admin'),
	(SELECT role_id FROM roles WHERE name = 'manage-users')
);

INSERT INTO user_role_link (user_id, role_id) VALUES (
	(SELECT user_id FROM users WHERE username = 'admin'),
	(SELECT role_id FROM roles WHERE name = 'article-admin')
);

INSERT INTO user_role_link (user_id, role_id) VALUES (
	(SELECT user_id FROM users WHERE username = 'admin'),
	(SELECT role_id FROM roles WHERE name = 'manage-tags')
);

INSERT INTO user_role_link (user_id, role_id) VALUES (
	(SELECT user_id FROM users WHERE username = 'admin'),
	(SELECT role_id FROM roles WHERE name = 'development-dwr')
);

-- Add test user, password 'test'
INSERT INTO users (username, password, email, enabled) VALUES (
	'test', 'a94a8fe5ccb19ba61c4c0873d391e987982fbbd3', 'test@randomcoder.com', true
);

INSERT INTO user_role_link (user_id, role_id) VALUES (
	(SELECT user_id FROM users WHERE username = 'test'),
	(SELECT role_id FROM roles WHERE name = 'article-post')
);

