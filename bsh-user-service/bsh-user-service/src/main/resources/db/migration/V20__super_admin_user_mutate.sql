-- V20: SUPER_ADMIN role + user mutate permissions. ASCII comments for H2.

INSERT INTO bsh_role (id, code, name, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'r0000000000000000000000000000005', 'SUPER_ADMIN', 'Super Admin',
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_role WHERE id = 'r0000000000000000000000000000005' OR code = 'SUPER_ADMIN');

INSERT INTO bsh_permission (id, code, name, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'p0000000000000000000000000000013', 'admin:user:write', 'admin user write',
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_permission WHERE id = 'p0000000000000000000000000000013' OR code = 'admin:user:write');

INSERT INTO bsh_permission (id, code, name, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'p0000000000000000000000000000014', 'admin:user:delete', 'admin user delete',
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_permission WHERE id = 'p0000000000000000000000000000014' OR code = 'admin:user:delete');

-- Copy current ADMIN permissions to SUPER_ADMIN (view/audit/config, not mutate).
INSERT INTO bsh_role_permission (role_id, permission_id)
SELECT 'r0000000000000000000000000000005', rp.permission_id
FROM bsh_role_permission rp
WHERE rp.role_id = 'r0000000000000000000000000000001'
  AND NOT EXISTS (
      SELECT 1 FROM bsh_role_permission x
      WHERE x.role_id = 'r0000000000000000000000000000005' AND x.permission_id = rp.permission_id
  );

INSERT INTO bsh_role_permission (role_id, permission_id)
SELECT 'r0000000000000000000000000000005', 'p0000000000000000000000000000013'
WHERE NOT EXISTS (
    SELECT 1 FROM bsh_role_permission
    WHERE role_id = 'r0000000000000000000000000000005' AND permission_id = 'p0000000000000000000000000000013'
);

INSERT INTO bsh_role_permission (role_id, permission_id)
SELECT 'r0000000000000000000000000000005', 'p0000000000000000000000000000014'
WHERE NOT EXISTS (
    SELECT 1 FROM bsh_role_permission
    WHERE role_id = 'r0000000000000000000000000000005' AND permission_id = 'p0000000000000000000000000000014'
);

INSERT INTO bsh_user (id, username, password_hash, phone, role, status, nickname, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'u0000000000000000000000000000006', 'sadmin', '{bcrypt-pending}', '13500000006', 'SUPER_ADMIN', 'ACTIVE', 'Super Admin',
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_user WHERE username = 'sadmin' AND deleted = 0);

-- SUPER_ADMIN for mutate; ADMIN so existing hasRole(ADMIN) and banner X-Roles still work.
INSERT INTO bsh_user_role (user_id, role_id)
SELECT u.id, r.id
FROM bsh_user u
JOIN bsh_role r ON r.code = 'SUPER_ADMIN' AND r.deleted = 0
WHERE u.username = 'sadmin' AND u.deleted = 0
  AND NOT EXISTS (
      SELECT 1 FROM bsh_user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

INSERT INTO bsh_user_role (user_id, role_id)
SELECT u.id, r.id
FROM bsh_user u
JOIN bsh_role r ON r.code = 'ADMIN' AND r.deleted = 0
WHERE u.username = 'sadmin' AND u.deleted = 0
  AND NOT EXISTS (
      SELECT 1 FROM bsh_user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );
