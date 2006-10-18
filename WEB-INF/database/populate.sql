-- Add security roles
INSERT INTO roles (name, description) VALUES ('manage-users', 'Manage users');
INSERT INTO roles (name, description) VALUES ('article-post', 'Post articles');
INSERT INTO roles (name, description) VALUES ('article-admin', 'Update other users'' articles');
INSERT INTO roles (name, description) VALUES ('development-dwr', 'DWR Testing');