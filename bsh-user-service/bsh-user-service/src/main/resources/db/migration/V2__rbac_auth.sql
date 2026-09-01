-- V2：登录字段 + RBAC 表与种子数据
-- admin 密码由启动时 AuthSeedRunner 用 BCrypt 写入/校正

ALTER TABLE bsh_user
    ADD COLUMN username VARCHAR(64) NULL COMMENT '登录名';

ALTER TABLE bsh_user
    ADD COLUMN password_hash VARCHAR(128) NULL COMMENT 'BCrypt 密码';

CREATE UNIQUE INDEX uk_bsh_user_username ON bsh_user (username);

CREATE TABLE IF NOT EXISTS bsh_role (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    code        VARCHAR(64)  NOT NULL COMMENT '角色编码',
    name        VARCHAR(64)  NOT NULL COMMENT '角色名称',
    PRIMARY KEY (id),
    UNIQUE KEY uk_bsh_role_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色';

CREATE TABLE IF NOT EXISTS bsh_permission (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    code        VARCHAR(64)  NOT NULL COMMENT '权限编码',
    name        VARCHAR(64)  NOT NULL COMMENT '权限名称',
    PRIMARY KEY (id),
    UNIQUE KEY uk_bsh_permission_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限';

CREATE TABLE IF NOT EXISTS bsh_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES bsh_user (id),
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES bsh_role (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bsh_role_permission (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES bsh_role (id),
    CONSTRAINT fk_rp_perm FOREIGN KEY (permission_id) REFERENCES bsh_permission (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO bsh_role (code, name) VALUES
    ('ADMIN', '系统管理员'),
    ('MERCHANT', '商家'),
    ('CONSUMER', '消费者')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO bsh_permission (code, name) VALUES
    ('admin:view', '后台访问'),
    ('admin:user', '用户管理'),
    ('banner:manage', '轮播管理'),
    ('merchant:supply', '供应管理')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO bsh_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM bsh_role r CROSS JOIN bsh_permission p
WHERE r.code = 'ADMIN'
ON DUPLICATE KEY UPDATE role_id = role_id;

INSERT INTO bsh_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM bsh_role r JOIN bsh_permission p ON p.code = 'merchant:supply'
WHERE r.code = 'MERCHANT'
ON DUPLICATE KEY UPDATE role_id = role_id;

INSERT INTO bsh_user (username, password_hash, phone, role, status, nickname)
SELECT 'admin', '{bcrypt-pending}', '13800000000', 'ADMIN', 'ACTIVE', '系统管理员'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM bsh_user WHERE username = 'admin');

INSERT INTO bsh_user_role (user_id, role_id)
SELECT u.id, r.id FROM bsh_user u JOIN bsh_role r ON r.code = 'ADMIN'
WHERE u.username = 'admin'
ON DUPLICATE KEY UPDATE user_id = user_id;
