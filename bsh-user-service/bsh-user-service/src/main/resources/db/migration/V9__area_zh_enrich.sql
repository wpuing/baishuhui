-- V9: Chinese area names + more provinces/cities/districts. UTF-8 data.

UPDATE bsh_area SET name = '北京市' WHERE code = '110000';
UPDATE bsh_area SET name = '天津市' WHERE code = '120000';
UPDATE bsh_area SET name = '河北省' WHERE code = '130000';
UPDATE bsh_area SET name = '上海市' WHERE code = '310000';
UPDATE bsh_area SET name = '江苏省' WHERE code = '320000';
UPDATE bsh_area SET name = '浙江省' WHERE code = '330000';
UPDATE bsh_area SET name = '山东省' WHERE code = '370000';
UPDATE bsh_area SET name = '广东省' WHERE code = '440000';
UPDATE bsh_area SET name = '重庆市' WHERE code = '500000';
UPDATE bsh_area SET name = '四川省' WHERE code = '510000';
UPDATE bsh_area SET name = '广州市' WHERE code = '440100';
UPDATE bsh_area SET name = '深圳市' WHERE code = '440300';
UPDATE bsh_area SET name = '天河区' WHERE code = '440106';
UPDATE bsh_area SET name = '杭州市' WHERE code = '330100';

INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1400000000000000000000000000000', NULL, '140000', '山西省', 1, 4, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '140000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1500000000000000000000000000000', NULL, '150000', '内蒙古自治区', 1, 5, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '150000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a2100000000000000000000000000000', NULL, '210000', '辽宁省', 1, 6, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '210000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a2200000000000000000000000000000', NULL, '220000', '吉林省', 1, 7, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '220000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a2300000000000000000000000000000', NULL, '230000', '黑龙江省', 1, 8, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '230000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3400000000000000000000000000000', NULL, '340000', '安徽省', 1, 12, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '340000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3500000000000000000000000000000', NULL, '350000', '福建省', 1, 13, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '350000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3600000000000000000000000000000', NULL, '360000', '江西省', 1, 14, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '360000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4100000000000000000000000000000', NULL, '410000', '河南省', 1, 16, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '410000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4200000000000000000000000000000', NULL, '420000', '湖北省', 1, 17, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '420000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4300000000000000000000000000000', NULL, '430000', '湖南省', 1, 18, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '430000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4500000000000000000000000000000', NULL, '450000', '广西壮族自治区', 1, 20, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '450000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4600000000000000000000000000000', NULL, '460000', '海南省', 1, 21, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '460000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5200000000000000000000000000000', NULL, '520000', '贵州省', 1, 24, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '520000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5300000000000000000000000000000', NULL, '530000', '云南省', 1, 25, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '530000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5400000000000000000000000000000', NULL, '540000', '西藏自治区', 1, 26, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '540000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a6100000000000000000000000000000', NULL, '610000', '陕西省', 1, 27, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '610000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a6200000000000000000000000000000', NULL, '620000', '甘肃省', 1, 28, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '620000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a6300000000000000000000000000000', NULL, '630000', '青海省', 1, 29, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '630000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a6400000000000000000000000000000', NULL, '640000', '宁夏回族自治区', 1, 30, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '640000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a6500000000000000000000000000000', NULL, '650000', '新疆维吾尔自治区', 1, 31, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '650000');

-- Beijing districts
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1101000000000000000000000000000', 'a1100000000000000000000000000000', '110100', '北京市辖区', 2, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '110100');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1101010000000000000000000000000', 'a1101000000000000000000000000000', '110101', '东城区', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '110101');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1101050000000000000000000000000', 'a1101000000000000000000000000000', '110105', '朝阳区', 3, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '110105');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1101080000000000000000000000000', 'a1101000000000000000000000000000', '110108', '海淀区', 3, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '110108');

-- Shanghai
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3101000000000000000000000000000', 'a3100000000000000000000000000000', '310100', '上海市辖区', 2, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '310100');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3101150000000000000000000000000', 'a3101000000000000000000000000000', '310115', '浦东新区', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '310115');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3101040000000000000000000000000', 'a3101000000000000000000000000000', '310104', '徐汇区', 3, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '310104');

-- Jiangsu cities
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3201000000000000000000000000000', 'a3200000000000000000000000000000', '320100', '南京市', 2, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '320100');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3205000000000000000000000000000', 'a3200000000000000000000000000000', '320500', '苏州市', 2, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '320500');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3202000000000000000000000000000', 'a3200000000000000000000000000000', '320200', '无锡市', 2, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '320200');

-- Zhejiang more
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3302000000000000000000000000000', 'a3300000000000000000000000000000', '330200', '宁波市', 2, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '330200');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3301060000000000000000000000000', 'a3301000000000000000000000000000', '330106', '西湖区', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '330106');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3301080000000000000000000000000', 'a3301000000000000000000000000000', '330108', '滨江区', 3, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '330108');

-- Shandong
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3701000000000000000000000000000', 'a3700000000000000000000000000000', '370100', '济南市', 2, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '370100');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3702000000000000000000000000000', 'a3700000000000000000000000000000', '370200', '青岛市', 2, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '370200');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3713000000000000000000000000000', 'a3700000000000000000000000000000', '371300', '临沂市', 2, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '371300');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3707000000000000000000000000000', 'a3700000000000000000000000000000', '370700', '潍坊市', 2, 4, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '370700');

-- Guangdong more
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4404000000000000000000000000000', 'a4400000000000000000000000000000', '440400', '珠海市', 2, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440400');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4406000000000000000000000000000', 'a4400000000000000000000000000000', '440600', '佛山市', 2, 4, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440600');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4419000000000000000000000000000', 'a4400000000000000000000000000000', '441900', '东莞市', 2, 5, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '441900');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4403050000000000000000000000000', 'a4403000000000000000000000000000', '440305', '南山区', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440305');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4403040000000000000000000000000', 'a4403000000000000000000000000000', '440304', '福田区', 3, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440304');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4401030000000000000000000000000', 'a4401000000000000000000000000000', '440103', '荔湾区', 3, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440103');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4401040000000000000000000000000', 'a4401000000000000000000000000000', '440104', '越秀区', 3, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440104');

-- Sichuan / Henan / Hunan produce hubs
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5101000000000000000000000000000', 'a5100000000000000000000000000000', '510100', '成都市', 2, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '510100');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5101040000000000000000000000000', 'a5101000000000000000000000000000', '510104', '锦江区', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '510104');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4101000000000000000000000000000', 'a4100000000000000000000000000000', '410100', '郑州市', 2, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '410100');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4113000000000000000000000000000', 'a4100000000000000000000000000000', '411300', '南阳市', 2, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '411300');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4301000000000000000000000000000', 'a4300000000000000000000000000000', '430100', '长沙市', 2, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '430100');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4302000000000000000000000000000', 'a4300000000000000000000000000000', '430200', '株洲市', 2, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '430200');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5001000000000000000000000000000', 'a5000000000000000000000000000000', '500100', '重庆市辖区', 2, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '500100');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5001030000000000000000000000000', 'a5001000000000000000000000000000', '500103', '渝中区', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '500103');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5001120000000000000000000000000', 'a5001000000000000000000000000000', '500112', '渝北区', 3, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '500112');
