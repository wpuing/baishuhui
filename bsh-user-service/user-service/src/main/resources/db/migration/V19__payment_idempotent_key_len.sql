-- V19: 退款作废幂等键 / 补偿退款键可能超过 64。
ALTER TABLE bsh_payment MODIFY COLUMN idempotent_key VARCHAR(128) NOT NULL;
