-- V3：预置商家账号（密码由 AuthSeedRunner 校正为 BCrypt）
INSERT INTO bsh_user (username, password_hash, phone, role, status, nickname)
SELECT 'merchant', '{bcrypt-pending}', '13900000001', 'MERCHANT', 'ACTIVE', '演示商家'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM bsh_user WHERE username = 'merchant');

INSERT INTO bsh_user_role (user_id, role_id)
SELECT u.id, r.id FROM bsh_user u JOIN bsh_role r ON r.code = 'MERCHANT'
WHERE u.username = 'merchant'
ON DUPLICATE KEY UPDATE user_id = user_id;
