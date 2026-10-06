-- V7: three portal demo accounts (passwords set by AuthSeedRunner). ASCII-only for H2.

INSERT INTO bsh_user (id, username, password_hash, phone, role, status, nickname, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'u0000000000000000000000000000003', 'user', '{bcrypt-pending}', '13700000002', 'CONSUMER', 'ACTIVE', '普通用户',
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_user WHERE username = 'user' AND deleted = 0);

INSERT INTO bsh_user (id, username, password_hash, phone, role, status, nickname, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'u0000000000000000000000000000004', 'boss', '{bcrypt-pending}', '13900000002', 'MERCHANT', 'ACTIVE', '商家老板',
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_user WHERE username = 'boss' AND deleted = 0);

INSERT INTO bsh_user_role (user_id, role_id)
SELECT u.id, r.id
FROM bsh_user u
JOIN bsh_role r ON r.code = 'CONSUMER' AND r.deleted = 0
WHERE u.username = 'user' AND u.deleted = 0
  AND NOT EXISTS (
      SELECT 1 FROM bsh_user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

INSERT INTO bsh_user_role (user_id, role_id)
SELECT u.id, r.id
FROM bsh_user u
JOIN bsh_role r ON r.code = 'MERCHANT' AND r.deleted = 0
WHERE u.username = 'boss' AND u.deleted = 0
  AND NOT EXISTS (
      SELECT 1 FROM bsh_user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );
