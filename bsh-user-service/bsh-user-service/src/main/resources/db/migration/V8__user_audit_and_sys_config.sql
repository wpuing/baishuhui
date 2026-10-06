-- V8: user audit columns, farmer role, sys_config, admin permissions. ASCII-only for H2.

ALTER TABLE bsh_user ADD COLUMN audit_remark VARCHAR(256) NULL;
ALTER TABLE bsh_user ADD COLUMN audit_time TIMESTAMP NULL;
ALTER TABLE bsh_user ADD COLUMN audit_user VARCHAR(32) NULL;

CREATE INDEX idx_bsh_user_status_ctime ON bsh_user (deleted, status, create_time, id);

INSERT INTO bsh_role (id, code, name, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
VALUES ('r0000000000000000000000000000004', 'FARMER', 'Farmer', CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026);

CREATE TABLE bsh_sys_config (
    id               VARCHAR(32)  NOT NULL,
    config_key       VARCHAR(128) NOT NULL,
    config_value     VARCHAR(1024) NOT NULL,
    value_type       VARCHAR(16)  NOT NULL DEFAULT 'STRING',
    group_code       VARCHAR(64)  NOT NULL DEFAULT 'DEFAULT',
    remark           VARCHAR(256) NULL,
    sync_redis       INT          NOT NULL DEFAULT 0,
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
    CONSTRAINT uk_bsh_sys_config_key UNIQUE (config_key)
);

CREATE INDEX idx_bsh_sys_config_group ON bsh_sys_config (deleted, group_code);

INSERT INTO bsh_sys_config (
    id, config_key, config_value, value_type, group_code, remark, sync_redis,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
) VALUES
('c0000000000000000000000000000001', 'job.visit.reset.cron', '0 0 0 * * ?', 'STRING', 'JOB', 'daily visit key rollover hint', 1,
 CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('c0000000000000000000000000000002', 'dict.user.audit.status', 'PENDING,REVIEWING,APPROVED,REJECTED', 'STRING', 'DICT', 'user audit status dictionary', 1,
 CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026);

INSERT INTO bsh_permission (id, code, name, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year) VALUES
('p0000000000000000000000000000006', 'admin:audit', 'admin user audit', CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('p0000000000000000000000000000007', 'admin:config', 'admin sys config', CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026),
('p0000000000000000000000000000008', 'admin:area', 'admin area manage', CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026);

INSERT INTO bsh_role_permission (role_id, permission_id) VALUES
('r0000000000000000000000000000001', 'p0000000000000000000000000000006'),
('r0000000000000000000000000000001', 'p0000000000000000000000000000007'),
('r0000000000000000000000000000001', 'p0000000000000000000000000000008');
