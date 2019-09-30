INSERT INTO oauth_client_details
	(client_id, client_secret, scope, authorized_grant_types,
	web_server_redirect_uri, authorities, access_token_validity,
	refresh_token_validity, additional_information, autoapprove)
SELECT DISTINCT
	'apiClient', '{bcrypt}$2a$10$R.g98j0Ls1RMwbAHl29Z2eoapjvttUPl0rC4nW831LwpQUCGo7PfO',
	'read,write,trust', 'password,authorization_code,refresh_token,client_credentials',
	null, null, 36000, 72000, null, false
FROM oauth_client_details
WHERE client_id = 'apiClient'
HAVING count(*) = 0;

INSERT INTO "user"
	(id, email, username, password, account_non_expired, account_non_locked, credentials_non_expired, enabled)
SELECT	
	nextval('user_id_seq'), 'admin@admin.com', 'admin', 
	'{bcrypt}$2a$10$u/VZfLWYp6MPrdRD3ACVnuIFprEx6l0oiw.vqLlewgYPIX/7PRKUu', 
	true, true, true, true
FROM "user"
WHERE email = 'admin@admin.com' AND username = 'admin'
HAVING count(*) = 0;

INSERT INTO role
	(id, role)
SELECT	
	nextval('role_id_seq'), 'ROLE_AS_ADMIN'
FROM role
WHERE role = 'ROLE_AS_ADMIN'
HAVING count(*) = 0;

INSERT INTO role
	(id, role)
SELECT	
	nextval('role_id_seq'), 'ROLE_AS_USER'
FROM role
WHERE role = 'ROLE_AS_USER'
HAVING count(*) = 0;

INSERT INTO user_role
	(id, user_id, role_id)
SELECT
	nextval('user_role_id_seq'),
	(SELECT u.id FROM "user" u WHERE u.email = 'admin@admin.com' AND u.username = 'admin') AS user_id,
	(SELECT r.id FROM role r WHERE r.role = 'ROLE_AS_ADMIN') AS role_id
FROM user_role ur
WHERE ur.user_id = user_id AND ur.role_id = role_id
HAVING count(*) = 0;