-- V6: IP whitelist / blacklist for gateway anti-abuse. ASCII-only for H2.

CREATE TABLE bsh_ip_rule (
    id               VARCHAR(32)  NOT NULL,
    ip               VARCHAR(64)  NOT NULL,
    rule_type        VARCHAR(16)  NOT NULL,
    source           VARCHAR(16)  NOT NULL,
    reason           VARCHAR(256) NULL,
    expire_time      TIMESTAMP    NULL,
    hit_count        INT          NOT NULL DEFAULT 0,
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

CREATE INDEX idx_bsh_ip_rule_type_ip ON bsh_ip_rule (deleted, rule_type, ip);
CREATE INDEX idx_bsh_ip_rule_expire ON bsh_ip_rule (deleted, expire_time);

INSERT INTO bsh_permission (id, code, name, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
VALUES ('p0000000000000000000000000000005', 'admin:ip', 'admin ip access', CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026);

INSERT INTO bsh_role_permission (role_id, permission_id)
VALUES ('r0000000000000000000000000000001', 'p0000000000000000000000000000005');
