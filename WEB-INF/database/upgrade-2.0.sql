-- Change role names to support Acegi naming conventions
UPDATE roles SET name = 'ROLE_MANAGE_USERS' WHERE name = 'manage-users';
UPDATE roles SET name = 'ROLE_POST_ARTICLES' WHERE name = 'article-post';
UPDATE roles SET name = 'ROLE_MANAGE_ARTICLES' WHERE name = 'article-admin';
UPDATE roles SET name = 'ROLE_DEVELOPMENT_DWR' WHERE name = 'development-dwr';
UPDATE roles SET name = 'ROLE_MANAGE_TAGS' WHERE name = 'manage-tags';
UPDATE roles SET name = 'ROLE_MANAGE_COMMENTS' WHERE name = 'manage-comments';