-- V10: more Chinese cities / districts for produce hubs (idempotent by code).

INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1101060000000000000000000000000', 'a1101000000000000000000000000000', '110106', '丰台区', 3, 4, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '110106');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1101070000000000000000000000000', 'a1101000000000000000000000000000', '110107', '石景山区', 3, 5, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '110107');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1101090000000000000000000000000', 'a1101000000000000000000000000000', '110109', '门头沟区', 3, 6, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '110109');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1101110000000000000000000000000', 'a1101000000000000000000000000000', '110111', '房山区', 3, 7, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '110111');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1101120000000000000000000000000', 'a1101000000000000000000000000000', '110112', '通州区', 3, 8, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '110112');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1101130000000000000000000000000', 'a1101000000000000000000000000000', '110113', '顺义区', 3, 9, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '110113');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1101140000000000000000000000000', 'a1101000000000000000000000000000', '110114', '昌平区', 3, 10, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '110114');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1101150000000000000000000000000', 'a1101000000000000000000000000000', '110115', '大兴区', 3, 11, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '110115');

INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3101010000000000000000000000000', 'a3101000000000000000000000000000', '310101', '黄浦区', 3, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '310101');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3101060000000000000000000000000', 'a3101000000000000000000000000000', '310106', '静安区', 3, 4, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '310106');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3101120000000000000000000000000', 'a3101000000000000000000000000000', '310112', '闵行区', 3, 5, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '310112');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3101130000000000000000000000000', 'a3101000000000000000000000000000', '310113', '宝山区', 3, 6, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '310113');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3101140000000000000000000000000', 'a3101000000000000000000000000000', '310114', '嘉定区', 3, 7, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '310114');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3101170000000000000000000000000', 'a3101000000000000000000000000000', '310117', '松江区', 3, 8, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '310117');

INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3201020000000000000000000000000', 'a3201000000000000000000000000000', '320102', '玄武区', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '320102');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3201040000000000000000000000000', 'a3201000000000000000000000000000', '320104', '秦淮区', 3, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '320104');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3201050000000000000000000000000', 'a3201000000000000000000000000000', '320105', '建邺区', 3, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '320105');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3201060000000000000000000000000', 'a3201000000000000000000000000000', '320106', '鼓楼区', 3, 4, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '320106');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3205050000000000000000000000000', 'a3205000000000000000000000000000', '320505', '虎丘区', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '320505');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3205080000000000000000000000000', 'a3205000000000000000000000000000', '320508', '姑苏区', 3, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '320508');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3205810000000000000000000000000', 'a3205000000000000000000000000000', '320581', '常熟市', 3, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '320581');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3203000000000000000000000000000', 'a3200000000000000000000000000000', '320300', '徐州市', 2, 4, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '320300');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3204000000000000000000000000000', 'a3200000000000000000000000000000', '320400', '常州市', 2, 5, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '320400');

INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3301020000000000000000000000000', 'a3301000000000000000000000000000', '330102', '上城区', 3, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '330102');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3301050000000000000000000000000', 'a3301000000000000000000000000000', '330105', '拱墅区', 3, 4, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '330105');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3301100000000000000000000000000', 'a3301000000000000000000000000000', '330110', '余杭区', 3, 5, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '330110');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3301110000000000000000000000000', 'a3301000000000000000000000000000', '330111', '富阳区', 3, 6, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '330111');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3302030000000000000000000000000', 'a3302000000000000000000000000000', '330203', '海曙区', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '330203');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3303000000000000000000000000000', 'a3300000000000000000000000000000', '330300', '温州市', 2, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '330300');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3304000000000000000000000000000', 'a3300000000000000000000000000000', '330400', '嘉兴市', 2, 4, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '330400');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3306000000000000000000000000000', 'a3300000000000000000000000000000', '330600', '绍兴市', 2, 5, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '330600');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3307000000000000000000000000000', 'a3300000000000000000000000000000', '330700', '金华市', 2, 6, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '330700');

INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3701020000000000000000000000000', 'a3701000000000000000000000000000', '370102', '历下区', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '370102');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3701030000000000000000000000000', 'a3701000000000000000000000000000', '370103', '市中区', 3, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '370103');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3702020000000000000000000000000', 'a3702000000000000000000000000000', '370202', '市南区', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '370202');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3702030000000000000000000000000', 'a3702000000000000000000000000000', '370203', '市北区', 3, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '370203');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3708000000000000000000000000000', 'a3700000000000000000000000000000', '370800', '济宁市', 2, 5, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '370800');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3710000000000000000000000000000', 'a3700000000000000000000000000000', '371000', '威海市', 2, 6, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '371000');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a3703000000000000000000000000000', 'a3700000000000000000000000000000', '370300', '淄博市', 2, 7, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '370300');

INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4401050000000000000000000000000', 'a4401000000000000000000000000000', '440105', '海珠区', 3, 4, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440105');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4401110000000000000000000000000', 'a4401000000000000000000000000000', '440111', '白云区', 3, 5, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440111');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4401120000000000000000000000000', 'a4401000000000000000000000000000', '440112', '黄埔区', 3, 6, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440112');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4401130000000000000000000000000', 'a4401000000000000000000000000000', '440113', '番禺区', 3, 7, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440113');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4403030000000000000000000000000', 'a4403000000000000000000000000000', '440303', '罗湖区', 3, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440303');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4403060000000000000000000000000', 'a4403000000000000000000000000000', '440306', '宝安区', 3, 4, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440306');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4403070000000000000000000000000', 'a4403000000000000000000000000000', '440307', '龙岗区', 3, 5, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440307');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4402000000000000000000000000000', 'a4400000000000000000000000000000', '440200', '韶关市', 2, 6, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440200');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4405000000000000000000000000000', 'a4400000000000000000000000000000', '440500', '汕头市', 2, 7, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440500');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4407000000000000000000000000000', 'a4400000000000000000000000000000', '440700', '江门市', 2, 8, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '440700');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4413000000000000000000000000000', 'a4400000000000000000000000000000', '441300', '惠州市', 2, 9, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '441300');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4420000000000000000000000000000', 'a4400000000000000000000000000000', '442000', '中山市', 2, 10, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '442000');

INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5101050000000000000000000000000', 'a5101000000000000000000000000000', '510105', '青羊区', 3, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '510105');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5101070000000000000000000000000', 'a5101000000000000000000000000000', '510107', '武侯区', 3, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '510107');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5101140000000000000000000000000', 'a5101000000000000000000000000000', '510114', '新都区', 3, 4, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '510114');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5107000000000000000000000000000', 'a5100000000000000000000000000000', '510700', '绵阳市', 2, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '510700');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5113000000000000000000000000000', 'a5100000000000000000000000000000', '511300', '南充市', 2, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '511300');

INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1301000000000000000000000000000', 'a1300000000000000000000000000000', '130100', '石家庄市', 2, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '130100');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1302000000000000000000000000000', 'a1300000000000000000000000000000', '130200', '唐山市', 2, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '130200');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1306000000000000000000000000000', 'a1300000000000000000000000000000', '130600', '保定市', 2, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '130600');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1201000000000000000000000000000', 'a1200000000000000000000000000000', '120100', '天津市辖区', 2, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '120100');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1201010000000000000000000000000', 'a1201000000000000000000000000000', '120101', '和平区', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '120101');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1201040000000000000000000000000', 'a1201000000000000000000000000000', '120104', '南开区', 3, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '120104');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a1201100000000000000000000000000', 'a1201000000000000000000000000000', '120110', '东丽区', 3, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '120110');

INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4101020000000000000000000000000', 'a4101000000000000000000000000000', '410102', '中原区', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '410102');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4101030000000000000000000000000', 'a4101000000000000000000000000000', '410103', '二七区', 3, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '410103');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4103000000000000000000000000000', 'a4100000000000000000000000000000', '410300', '洛阳市', 2, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '410300');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4301020000000000000000000000000', 'a4301000000000000000000000000000', '430102', '芙蓉区', 3, 1, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '430102');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a4301040000000000000000000000000', 'a4301000000000000000000000000000', '430104', '岳麓区', 3, 2, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '430104');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5001050000000000000000000000000', 'a5001000000000000000000000000000', '500105', '江北区', 3, 3, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '500105');
INSERT INTO bsh_area (id, parent_id, code, name, level, sort_no, create_time, create_user, create_user_name, update_time, update_user, deleted, data_year)
SELECT 'a5001070000000000000000000000000', 'a5001000000000000000000000000000', '500107', '九龙坡区', 3, 4, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 0, 2026
WHERE NOT EXISTS (SELECT 1 FROM bsh_area WHERE code = '500107');
