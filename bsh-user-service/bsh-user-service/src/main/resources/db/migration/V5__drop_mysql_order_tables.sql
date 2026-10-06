-- V5: trade orders live in MongoDB; drop leftover MySQL order tables if present.
-- ASCII-only for H2 local compatibility.

DROP TABLE IF EXISTS bsh_order_item;
DROP TABLE IF EXISTS bsh_order;
