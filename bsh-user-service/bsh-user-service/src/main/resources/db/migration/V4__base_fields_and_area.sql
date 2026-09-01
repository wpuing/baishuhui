-- V4: base audit columns + VARCHAR(32) PK + area table
-- ASCII-only for H2 local compatibility; MySQL also accepts this dialect.

DROP TABLE IF EXISTS bsh_role_permission;
DROP TABLE IF EXISTS bsh_user_role;
DROP TABLE IF EXISTS bsh_order_item;
DROP TABLE IF EXISTS bsh_order;
DROP TABLE IF EXISTS bsh_permission;
DROP TABLE IF EXISTS bsh_role;
DROP TABLE IF EXISTS bsh_merchant;
DROP TABLE IF EXISTS bsh_user;
DROP TABLE IF EXISTS bsh_area;

CREATE TABLE bsh_user (
    id               VARCHAR(32)  NOT NULL,
    username         VARCHAR(64)  NULL,
    password_hash    VARCHAR(128) NULL,
    phone            VARCHAR(20)  NOT NULL,
    role             VARCHAR(32)  NOT NULL DEFAULT 'CONSUMER',
    status           VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    nickname         VARCHAR(64)  DEFAULT NULL,
    create_time      TIMESTAMP    NOT NULL,
    create_user      VARCHAR(32)  NOT NULL,
    create_user_name VARCHAR(64)  NOT NULL,
    update_time      TIMESTAMP    NOT NULL,
    update_user      VARCHAR(32)  NOT NULL,
    deleted          INT          NOT NULL DEFAULT 0,
    delete_time      TIMESTAMP    NULL,
    area             VARCHAR(32)  NULL,
    data_year        INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_bsh_user_phone UNIQUE (phone),
    CONSTRAINT uk_bsh_user_username UNIQUE (username)
);

CREATE TABLE bsh_merchant (
    id               VARCHAR(32)  NOT NULL,
    phone            VARCHAR(20)  NOT NULL,
    shop_name        VARCHAR(128) NOT NULL,
    status           VARCHAR(32)  NOT NULL DEFAULT 'PENDING',
    create_time      TIMESTAMP    NOT NULL,
    create_user      VARCHAR(32)  NOT NULL,
    create_user_name VARCHAR(64)  NOT NULL,
    update_time      TIMESTAMP    NOT NULL,
    update_user      VARCHAR(32)  NOT NULL,
    deleted          INT          NOT NULL DEFAULT 0,
    delete_time      TIMESTAMP    NULL,
    area             VARCHAR(32)  NULL,
    data_year        INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_bsh_merchant_phone UNIQUE (phone)
);

CREATE TABLE bsh_role (
    id               VARCHAR(32)  NOT NULL,
    code             VARCHAR(64)  NOT NULL,
    name             VARCHAR(64)  NOT NULL,
    create_time      TIMESTAMP    NOT NULL,
    create_user      VARCHAR(32)  NOT NULL,
    create_user_name VARCHAR(64)  NOT NULL,
    update_time      TIMESTAMP    NOT NULL,
    update_user      VARCHAR(32)  NOT NULL,
    deleted          INT          NOT NULL DEFAULT 0,
    delete_time      TIMESTAMP    NULL,
    area             VARCHAR(32)  NULL,
    data_year        INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_bsh_role_code UNIQUE (code)
);

CREATE TABLE bsh_permission (
    id               VARCHAR(32)  NOT NULL,
    code             VARCHAR(64)  NOT NULL,
    name             VARCHAR(64)  NOT NULL,
    create_time      TIMESTAMP    NOT NULL,
    create_user      VARCHAR(32)  NOT NULL,
    create_user_name VARCHAR(64)  NOT NULL,
    update_time      TIMESTAMP    NOT NULL,
    update_user      VARCHAR(32)  NOT NULL,
    deleted          INT          NOT NULL DEFAULT 0,
    delete_time      TIMESTAMP    NULL,
    area             VARCHAR(32)  NULL,
    data_year        INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_bsh_permission_code UNIQUE (code)
);

CREATE TABLE bsh_user_role (
    user_id VARCHAR(32) NOT NULL,
    role_id VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES bsh_user (id),
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES bsh_role (id)
);

CREATE TABLE bsh_role_permission (
    role_id       VARCHAR(32) NOT NULL,
    permission_id VARCHAR(32) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES bsh_role (id),
    CONSTRAINT fk_rp_perm FOREIGN KEY (permission_id) REFERENCES bsh_permission (id)
);

CREATE TABLE bsh_order (
    id               VARCHAR(32)    NOT NULL,
    order_no         VARCHAR(64)    NOT NULL,
    buyer_id         VARCHAR(32)    NOT NULL,
    seller_id        VARCHAR(32)    NOT NULL,
    amount           DECIMAL(18, 2) NOT NULL,
    status           VARCHAR(32)    NOT NULL DEFAULT 'CREATED',
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
    CONSTRAINT uk_bsh_order_no UNIQUE (order_no)
);

CREATE TABLE bsh_order_item (
    id               VARCHAR(32)    NOT NULL,
    order_id         VARCHAR(32)    NOT NULL,
    sku              VARCHAR(64)    NOT NULL,
    product_name     VARCHAR(128)   NOT NULL,
    quantity         DECIMAL(18, 3) NOT NULL,
    unit_price       DECIMAL(18, 2) NOT NULL,
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
    CONSTRAINT fk_bsh_order_item_order FOREIGN KEY (order_id) REFERENCES bsh_order (id)
);

CREATE TABLE bsh_area (
    id               VARCHAR(32)  NOT NULL,
    parent_id        VARCHAR(32)  NULL,
    code             VARCHAR(32)  NOT NULL,
    name             VARCHAR(64)  NOT NULL,
    level            INT          NOT NULL,
    sort_no          INT          NOT NULL DEFAULT 0,
    create_time      TIMESTAMP    NOT NULL,
    create_user      VARCHAR(32)  NOT NULL,
    create_user_name VARCHAR(64)  NOT NULL,
    update_time      TIMESTAMP    NOT NULL,
    update_user      VARCHAR(32)  NOT NULL,
    deleted          INT          NOT NULL DEFAULT 0,
    delete_time      TIMESTAMP    NULL,
    area             VARCHAR(32)  NULL,
    data_year        INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_bsh_area_code UNIQUE (code)
);

INSERT INTO bsh_role (id, code, name, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year) VALUES
('r0000000000000000000000000000001', 'ADMIN', 'Admin', CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('r0000000000000000000000000000002', 'MERCHANT', 'Merchant', CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('r0000000000000000000000000000003', 'CONSUMER', 'Consumer', CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026);

INSERT INTO bsh_permission (id, code, name, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year) VALUES
('p0000000000000000000000000000001', 'admin:view', 'admin view', CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('p0000000000000000000000000000002', 'admin:user', 'admin user', CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('p0000000000000000000000000000003', 'banner:manage', 'banner manage', CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('p0000000000000000000000000000004', 'merchant:supply', 'merchant supply', CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026);

INSERT INTO bsh_role_permission (role_id, permission_id)
SELECT 'r0000000000000000000000000000001', id FROM bsh_permission;

INSERT INTO bsh_role_permission (role_id, permission_id) VALUES
('r0000000000000000000000000000002', 'p0000000000000000000000000000004');

INSERT INTO bsh_user (id, username, password_hash, phone, role, status, nickname, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year) VALUES
('u0000000000000000000000000000001', 'admin', '{bcrypt-pending}', '13800000000', 'ADMIN', 'ACTIVE', 'Admin', CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('u0000000000000000000000000000002', 'merchant', '{bcrypt-pending}', '13900000001', 'MERCHANT', 'ACTIVE', 'Merchant', CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026);

INSERT INTO bsh_user_role (user_id, role_id) VALUES
('u0000000000000000000000000000001', 'r0000000000000000000000000000001'),
('u0000000000000000000000000000002', 'r0000000000000000000000000000002');

INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year) VALUES
('a1100000000000000000000000000000', NULL, '110000', 'Beijing', 1, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('a1200000000000000000000000000000', NULL, '120000', 'Tianjin', 1, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('a1300000000000000000000000000000', NULL, '130000', 'Hebei', 1, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('a3100000000000000000000000000000', NULL, '310000', 'Shanghai', 1, 9, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('a3200000000000000000000000000000', NULL, '320000', 'Jiangsu', 1, 10, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('a3300000000000000000000000000000', NULL, '330000', 'Zhejiang', 1, 11, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('a3700000000000000000000000000000', NULL, '370000', 'Shandong', 1, 15, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('a4400000000000000000000000000000', NULL, '440000', 'Guangdong', 1, 19, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('a5000000000000000000000000000000', NULL, '500000', 'Chongqing', 1, 22, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('a5100000000000000000000000000000', NULL, '510000', 'Sichuan', 1, 23, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('a4401000000000000000000000000000', 'a4400000000000000000000000000000', '440100', 'Guangzhou', 2, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('a4403000000000000000000000000000', 'a4400000000000000000000000000000', '440300', 'Shenzhen', 2, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('a4401060000000000000000000000000', 'a4401000000000000000000000000000', '440106', 'Tianhe', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('a3301000000000000000000000000000', 'a3300000000000000000000000000000', '330100', 'Hangzhou', 2, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026);
