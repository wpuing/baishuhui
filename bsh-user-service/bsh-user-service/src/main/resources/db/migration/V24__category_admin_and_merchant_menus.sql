-- V24: admin category permission + merchant wallet/notification menus.

INSERT INTO bsh_permission (id, code, name, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'p0000000000000000000000000000015', 'admin:category', 'admin category manage',
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_permission WHERE id = 'p0000000000000000000000000000015' OR code = 'admin:category');

INSERT INTO bsh_role_permission (role_id, permission_id)
SELECT 'r0000000000000000000000000000001', 'p0000000000000000000000000000015'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_permission
  WHERE role_id = 'r0000000000000000000000000000001' AND permission_id = 'p0000000000000000000000000000015'
);

INSERT INTO bsh_role_permission (role_id, permission_id)
SELECT 'r0000000000000000000000000000005', 'p0000000000000000000000000000015'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_permission
  WHERE role_id = 'r0000000000000000000000000000005' AND permission_id = 'p0000000000000000000000000000015'
);

INSERT INTO bsh_menu (
    id, parent_id, client_type, name, path, icon, sort_no, menu_type, permission_code, visible,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'm0000000000000000000000000000015', NULL, 'MERCHANT', '我的账户', '/wallet', NULL, 35, 'MENU', NULL, 1,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_menu WHERE id = 'm0000000000000000000000000000015');

INSERT INTO bsh_menu (
    id, parent_id, client_type, name, path, icon, sort_no, menu_type, permission_code, visible,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'm0000000000000000000000000000016', NULL, 'MERCHANT', '消息中心', '/notifications', NULL, 36, 'MENU', NULL, 1,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_menu WHERE id = 'm0000000000000000000000000000016');

INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000002', 'm0000000000000000000000000000015'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu
  WHERE role_id = 'r0000000000000000000000000000002' AND menu_id = 'm0000000000000000000000000000015'
);

INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000002', 'm0000000000000000000000000000016'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu
  WHERE role_id = 'r0000000000000000000000000000002' AND menu_id = 'm0000000000000000000000000000016'
);

INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000001', 'm0000000000000000000000000000015'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu
  WHERE role_id = 'r0000000000000000000000000000001' AND menu_id = 'm0000000000000000000000000000015'
);

-- Also bind FARMER (demo seller often uses farmer account on merchant menus).
INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000004', 'm0000000000000000000000000000015'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu
  WHERE role_id = 'r0000000000000000000000000000004' AND menu_id = 'm0000000000000000000000000000015'
);

INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000004', 'm0000000000000000000000000000016'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu
  WHERE role_id = 'r0000000000000000000000000000004' AND menu_id = 'm0000000000000000000000000000016'
);
