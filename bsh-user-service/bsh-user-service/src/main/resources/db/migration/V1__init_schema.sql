-- V1__init_schema.sql
-- 百蔬汇 MySQL 初始化建表（Flyway）
-- 对应领域：User / Order

CREATE TABLE IF NOT EXISTS bsh_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    phone       VARCHAR(20)  NOT NULL COMMENT '手机号',
    role        VARCHAR(32)  NOT NULL DEFAULT 'CONSUMER' COMMENT '角色',
    status      VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    nickname    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_bsh_user_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS bsh_merchant (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    phone       VARCHAR(20)  NOT NULL COMMENT '手机号',
    shop_name   VARCHAR(128) NOT NULL COMMENT '店铺名称',
    status      VARCHAR(32)  NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_bsh_merchant_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

CREATE TABLE IF NOT EXISTS bsh_order (
    id          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_no    VARCHAR(64)    NOT NULL COMMENT '订单号',
    buyer_id    BIGINT         NOT NULL COMMENT '买家ID',
    seller_id   BIGINT         NOT NULL COMMENT '卖家ID',
    amount      DECIMAL(18, 2) NOT NULL COMMENT '订单金额',
    status      VARCHAR(32)    NOT NULL DEFAULT 'CREATED' COMMENT '状态',
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_bsh_order_no (order_no),
    KEY idx_bsh_order_buyer (buyer_id),
    KEY idx_bsh_order_seller (seller_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE IF NOT EXISTS bsh_order_item (
    id           BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id     BIGINT         NOT NULL COMMENT '订单ID',
    sku          VARCHAR(64)    NOT NULL COMMENT 'SKU',
    product_name VARCHAR(128)   NOT NULL COMMENT '商品名',
    quantity     DECIMAL(18, 3) NOT NULL COMMENT '数量',
    unit_price   DECIMAL(18, 2) NOT NULL COMMENT '单价',
    PRIMARY KEY (id),
    KEY idx_bsh_order_item_order (order_id),
    CONSTRAINT fk_bsh_order_item_order FOREIGN KEY (order_id) REFERENCES bsh_order (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';
