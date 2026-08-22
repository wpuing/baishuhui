-- V15: seed test wallet for demo portal accounts (SYSTEM 500000). ASCII for H2.

-- Ensure channels exist for demo consumer `user`
INSERT INTO bsh_wallet_channel (
    id, user_id, channel, balance, version,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'wch00000000000000000000000000001', 'u0000000000000000000000000000003', 'SYSTEM', 500000.00, 0,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (
    SELECT 1 FROM bsh_wallet_channel WHERE user_id = 'u0000000000000000000000000000003' AND channel = 'SYSTEM' AND deleted = 0
);

INSERT INTO bsh_wallet_channel (
    id, user_id, channel, balance, version,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'wch00000000000000000000000000002', 'u0000000000000000000000000000003', 'ALIPAY', 0.00, 0,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (
    SELECT 1 FROM bsh_wallet_channel WHERE user_id = 'u0000000000000000000000000000003' AND channel = 'ALIPAY' AND deleted = 0
);

INSERT INTO bsh_wallet_channel (
    id, user_id, channel, balance, version,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'wch00000000000000000000000000003', 'u0000000000000000000000000000003', 'WECHAT', 0.00, 0,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (
    SELECT 1 FROM bsh_wallet_channel WHERE user_id = 'u0000000000000000000000000000003' AND channel = 'WECHAT' AND deleted = 0
);

INSERT INTO bsh_wallet_channel (
    id, user_id, channel, balance, version,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'wch00000000000000000000000000004', 'u0000000000000000000000000000003', 'BANK', 0.00, 0,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (
    SELECT 1 FROM bsh_wallet_channel WHERE user_id = 'u0000000000000000000000000000003' AND channel = 'BANK' AND deleted = 0
);

INSERT INTO bsh_wallet_channel (
    id, user_id, channel, balance, version,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'wch00000000000000000000000000005', 'u0000000000000000000000000000003', 'CREDIT', 0.00, 0,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (
    SELECT 1 FROM bsh_wallet_channel WHERE user_id = 'u0000000000000000000000000000003' AND channel = 'CREDIT' AND deleted = 0
);

INSERT INTO bsh_wallet_ledger (
    id, user_id, channel, biz_type, direction, amount, balance_before, balance_after,
    order_id, payment_id, remark,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'wlg00000000000000000000000000001', 'u0000000000000000000000000000003', 'SYSTEM', 'TEST_GRANT', 'REFUND',
       500000.00, 0.00, 500000.00, NULL, NULL, 'demo user seed',
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (
    SELECT 1 FROM bsh_wallet_ledger WHERE user_id = 'u0000000000000000000000000000003' AND biz_type = 'TEST_GRANT' AND deleted = 0
);
