-- V16: demo farmer account + wallet for cross-account trade simulation. ASCII for H2.

INSERT INTO bsh_user (id, username, password_hash, phone, role, status, nickname, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'u0000000000000000000000000000005', 'farmer', '{bcrypt-pending}', '13600000005', 'FARMER', 'ACTIVE', '演示农户',
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_user WHERE username = 'farmer' AND deleted = 0);

INSERT INTO bsh_user_role (user_id, role_id)
SELECT u.id, r.id
FROM bsh_user u
JOIN bsh_role r ON r.code = 'FARMER' AND r.deleted = 0
WHERE u.username = 'farmer' AND u.deleted = 0
  AND NOT EXISTS (
      SELECT 1 FROM bsh_user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- Wallet channels for farmer (SYSTEM grant 500000 for demo)
INSERT INTO bsh_wallet_channel (
    id, user_id, channel, balance, version,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'wch00000000000000000000000000011', 'u0000000000000000000000000000005', 'SYSTEM', 500000.00, 0,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (
    SELECT 1 FROM bsh_wallet_channel WHERE user_id = 'u0000000000000000000000000000005' AND channel = 'SYSTEM' AND deleted = 0
);

INSERT INTO bsh_wallet_channel (
    id, user_id, channel, balance, version,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'wch00000000000000000000000000012', 'u0000000000000000000000000000005', 'ALIPAY', 0.00, 0,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (
    SELECT 1 FROM bsh_wallet_channel WHERE user_id = 'u0000000000000000000000000000005' AND channel = 'ALIPAY' AND deleted = 0
);

INSERT INTO bsh_wallet_channel (
    id, user_id, channel, balance, version,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'wch00000000000000000000000000013', 'u0000000000000000000000000000005', 'WECHAT', 0.00, 0,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (
    SELECT 1 FROM bsh_wallet_channel WHERE user_id = 'u0000000000000000000000000000005' AND channel = 'WECHAT' AND deleted = 0
);

INSERT INTO bsh_wallet_channel (
    id, user_id, channel, balance, version,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'wch00000000000000000000000000014', 'u0000000000000000000000000000005', 'BANK', 0.00, 0,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (
    SELECT 1 FROM bsh_wallet_channel WHERE user_id = 'u0000000000000000000000000000005' AND channel = 'BANK' AND deleted = 0
);

INSERT INTO bsh_wallet_channel (
    id, user_id, channel, balance, version,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'wch00000000000000000000000000015', 'u0000000000000000000000000000005', 'CREDIT', 0.00, 0,
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (
    SELECT 1 FROM bsh_wallet_channel WHERE user_id = 'u0000000000000000000000000000005' AND channel = 'CREDIT' AND deleted = 0
);

INSERT INTO bsh_wallet_ledger (
    id, user_id, channel, biz_type, direction, amount, balance_before, balance_after,
    order_id, payment_id, remark,
    create_time, create_user, create_user_name, update_time, update_user, deleted, data_year
)
SELECT 'wlg00000000000000000000000000011', 'u0000000000000000000000000000005', 'SYSTEM', 'TEST_GRANT', 'REFUND',
       500000.00, 0.00, 500000.00, NULL, NULL, 'demo farmer seed',
       CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (
    SELECT 1 FROM bsh_wallet_ledger WHERE user_id = 'u0000000000000000000000000000005' AND biz_type = 'TEST_GRANT' AND deleted = 0
);
