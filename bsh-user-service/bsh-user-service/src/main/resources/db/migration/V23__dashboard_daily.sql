-- V23: daily dashboard snapshot for historical overview.

CREATE TABLE bsh_dashboard_daily (
    id               VARCHAR(32)  NOT NULL,
    stat_date        DATE         NOT NULL,
    login_count      BIGINT       NOT NULL DEFAULT 0,
    pending_audit    BIGINT       NOT NULL DEFAULT 0,
    published_supply BIGINT       NOT NULL DEFAULT 0,
    banner_count     BIGINT       NOT NULL DEFAULT 0,
    audit_pie_json   VARCHAR(1024) NULL,
    category_rank_json VARCHAR(2048) NULL,
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

CREATE UNIQUE INDEX uk_bsh_dashboard_daily_date ON bsh_dashboard_daily (stat_date);
CREATE INDEX idx_bsh_dashboard_daily_deleted ON bsh_dashboard_daily (deleted, stat_date);
