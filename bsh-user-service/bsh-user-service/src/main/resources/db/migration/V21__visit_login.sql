-- V21: persist each successful login for dashboard 7-day trend.
-- Redis TTL previously expired at next midnight, so past days always showed 0.

CREATE TABLE bsh_visit_login (
    id               VARCHAR(32)  NOT NULL,
    user_id          VARCHAR(32)  NOT NULL,
    username         VARCHAR(64)  NULL,
    client_ip        VARCHAR(64)  NULL,
    login_time       TIMESTAMP    NOT NULL,
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

CREATE INDEX idx_bsh_visit_login_time ON bsh_visit_login (deleted, login_time);
