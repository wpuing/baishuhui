-- V14: wallet channels/ledgers/payments + consumer wallet menu. ASCII for H2.

CREATE TABLE IF NOT EXISTS bsh_wallet_channel (
    id               VARCHAR(32)    NOT NULL,
    user_id          VARCHAR(32)    NOT NULL,
    channel          VARCHAR(32)    NOT NULL,
    balance          DECIMAL(18,2)  NOT NULL DEFAULT 0,
    version          INT            NOT NULL DEFAULT 0,
    create_time      TIMESTAMP      NOT NULL,
    create_user      VARCHAR(32)    NOT NULL,
    create_user_name VARCHAR(64)    NOT NULL,
    update_time      TIMESTAMP      NOT NULL,
    update_user      VARCHAR(32)    NOT NULL,
    deleted          INT            NOT NULL DEFAULT 0,
    delete_time      TIMESTAMP      NULL,
    area             VARCHAR(32)    NULL,
    data_year        INT            NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_wallet_channel_user UNIQUE (user_id, channel)
);

CREATE TABLE IF NOT EXISTS bsh_wallet_ledger (
    id               VARCHAR(32)    NOT NULL,
    user_id          VARCHAR(32)    NOT NULL,
    channel          VARCHAR(32)    NOT NULL,
    biz_type         VARCHAR(32)    NOT NULL,
    direction        VARCHAR(16)    NOT NULL,
    amount           DECIMAL(18,2)  NOT NULL,
    balance_before   DECIMAL(18,2)  NOT NULL,
    balance_after    DECIMAL(18,2)  NOT NULL,
    order_id         VARCHAR(32)    NULL,
    payment_id       VARCHAR(32)    NULL,
    remark           VARCHAR(256)   NULL,
    create_time      TIMESTAMP      NOT NULL,
    create_user      VARCHAR(32)    NOT NULL,
    create_user_name VARCHAR(64)    NOT NULL,
    update_time      TIMESTAMP      NOT NULL,
    update_user      VARCHAR(32)    NOT NULL,
    deleted          INT            NOT NULL DEFAULT 0,
    delete_time      TIMESTAMP      NULL,
    area             VARCHAR(32)    NULL,
    data_year        INT            NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bsh_payment (
    id               VARCHAR(32)    NOT NULL,
    user_id          VARCHAR(32)    NOT NULL,
    order_id         VARCHAR(32)    NULL,
    channel          VARCHAR(32)    NOT NULL,
    amount           DECIMAL(18,2)  NOT NULL,
    direction        VARCHAR(16)    NOT NULL,
    biz_type         VARCHAR(32)    NOT NULL,
    status           VARCHAR(16)    NOT NULL,
    idempotent_key   VARCHAR(64)    NOT NULL,
    related_payment_id VARCHAR(32)  NULL,
    create_time      TIMESTAMP      NOT NULL,
    create_user      VARCHAR(32)    NOT NULL,
    create_user_name VARCHAR(64)    NOT NULL,
    update_time      TIMESTAMP      NOT NULL,
    update_user      VARCHAR(32)    NOT NULL,
    deleted          INT            NOT NULL DEFAULT 0,
    delete_time      TIMESTAMP      NULL,
    area             VARCHAR(32)    NULL,
    data_year        INT            NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_payment_idempotent UNIQUE (idempotent_key)
);

INSERT INTO bsh_menu (
    id, parent_id, client_type, name, path, icon, sort_no, menu_type, permission_code, visible,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'm0000000000000000000000000000012', NULL, 'CONSUMER', '我的账户', '/pages/mine/wallet', NULL, 45, 'MENU', 'consumer:wallet', 1,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_menu WHERE id = 'm0000000000000000000000000000012');

INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000003', 'm0000000000000000000000000000012'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu WHERE role_id = 'r0000000000000000000000000000003' AND menu_id = 'm0000000000000000000000000000012'
);
INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000004', 'm0000000000000000000000000000012'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu WHERE role_id = 'r0000000000000000000000000000004' AND menu_id = 'm0000000000000000000000000000012'
);
INSERT INTO bsh_role_menu (role_id, menu_id)
SELECT 'r0000000000000000000000000000001', 'm0000000000000000000000000000012'
WHERE NOT EXISTS (
  SELECT 1 FROM bsh_role_menu WHERE role_id = 'r0000000000000000000000000000001' AND menu_id = 'm0000000000000000000000000000012'
);
