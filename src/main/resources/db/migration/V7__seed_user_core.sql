-- ROLES (3 perfis)
INSERT INTO roles (id, authority)
VALUES (1, 'ROLE_ADMIN');

-- USERS (com array de roles)
INSERT INTO users (id, username, password)
VALUES
    (1, 'admin', '$2a$12$aTPyYalKFBsS.A/L7koNruqIkZLWCeY4/YxfgNkopxiSYq1nesNK2');


INSERT INTO user_roles (user_id, role_id)
VALUES
    (1, 1);

SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
