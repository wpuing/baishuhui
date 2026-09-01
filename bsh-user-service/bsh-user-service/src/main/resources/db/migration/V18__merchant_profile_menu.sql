-- V18: merchant profile menu.

INSERT INTO bsh_menu (
    id, parent_id, client_type, name, path, icon, sort_no, menu_type, permission_code, visible,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'm0000000000000000000000000000014', NULL, 'MERCHANT', '个人信息', '/profile', NULL, 40, 'MENU', NULL, 1,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_menu WHERE id = 'm0000000000000000000000000000014');

INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000002', 'm0000000000000000000000000000014'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu
  WHERE role_id = 'r0000000000000000000000000000002' AND menu_id = 'm0000000000000000000000000000014'
);

INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000001', 'm0000000000000000000000000000014'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu
  WHERE role_id = 'r0000000000000000000000000001' AND menu_id = 'm0000000000000000000000000000014'
);
