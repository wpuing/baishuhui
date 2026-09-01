-- V22: in-app trade notifications (inbox).

CREATE TABLE bsh_notification (
    id               VARCHAR(32)  NOT NULL,
    user_id          VARCHAR(32)  NOT NULL,
    msg_type         VARCHAR(32)  NOT NULL,
    title            VARCHAR(128) NOT NULL,
    content          VARCHAR(512) NOT NULL,
    biz_type         VARCHAR(32)  NULL,
    biz_id           VARCHAR(32)  NULL,
    read_flag        INT          NOT NULL DEFAULT 0,
    create_time      TIMESTAMP    NOT NULL,
    create_user      VARCHAR(32)  NOT NULL,
    create_user_name VARCHAR(64)  NOT NULL,
    update_time      TIMESTAMP    NOT NULL,
    update_user      VARCHAR(32)  NOT NULL,
    deleted          INT          NOT NULL DEFAULT 0,
    delete_time      TIMESTAMP    NULL,
    area             VARCHAR(32)  NULL,
    data_year        INT          NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_bsh_notification_user_time ON bsh_notification (deleted, user_id, create_time);
CREATE INDEX idx_bsh_notification_user_unread ON bsh_notification (deleted, user_id, read_flag);

-- Consumer mine menu: notifications + favorites
INSERT INTO bsh_menu (
    id, parent_id, client_type, name, path, icon, sort_no, menu_type, permission_code, visible,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'm0000000000000000000000000000012', NULL, 'CONSUMER', '消息中心', '/pages/mine/notifications', NULL, 35, 'MENU', NULL, 1,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_menu WHERE id = 'm0000000000000000000000000000012');

INSERT INTO bsh_menu (
    id, parent_id, client_type, name, path, icon, sort_no, menu_type, permission_code, visible,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'm0000000000000000000000000000013', NULL, 'CONSUMER', '我的收藏', '/pages/mine/favorites', NULL, 36, 'MENU', NULL, 1,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_menu WHERE id = 'm0000000000000000000000000000013');

INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000003', 'm0000000000000000000000000000012'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu WHERE role_id = 'r0000000000000000000000000000003' AND menu_id = 'm0000000000000000000000000000012'
);
INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000003', 'm0000000000000000000000000000013'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu WHERE role_id = 'r0000000000000000000000000000003' AND menu_id = 'm0000000000000000000000000000013'
);
INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000004', 'm0000000000000000000000000000012'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu WHERE role_id = 'r0000000000000000000000000000004' AND menu_id = 'm0000000000000000000000000000012'
);
INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000004', 'm0000000000000000000000000000013'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu WHERE role_id = 'r0000000000000000000000000000004' AND menu_id = 'm0000000000000000000000000000013'
);
INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000001', 'm0000000000000000000000000000012'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu WHERE role_id = 'r0000000000000000000000000000001' AND menu_id = 'm0000000000000000000000000000012'
);
INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000001', 'm0000000000000000000000000000013'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu WHERE role_id = 'r0000000000000000000000000000001' AND menu_id = 'm0000000000000000000000000000013'
);
