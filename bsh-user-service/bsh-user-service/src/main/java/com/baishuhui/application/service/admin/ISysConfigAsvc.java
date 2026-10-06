package com.baishuhui.application.service.admin;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.user.vo.admin.PageResultDTO;
import com.baishuhui.user.vo.admin.RedisKvRequest;
import com.baishuhui.user.vo.admin.SysConfigDTO;
import com.baishuhui.user.vo.admin.UpsertSysConfigRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 应用服务接口（原 ISysConfigAsvc）。
 *
 * @author wei yz
 */
public interface ISysConfigAsvc {
    void warmRedis();
    PageResultDTO<SysConfigDTO> page(String groupCode, int pageNum, int pageSize);
    SysConfigDTO create(UpsertSysConfigRequest request);
    SysConfigDTO update(String id, UpsertSysConfigRequest request);
    void delete(String id);
    List<String> listRedisKeys(String prefix);
    Map<String, String> getRedisValue(String key);
    void setRedisValue(RedisKvRequest request);
    void deleteRedisKey(String key);
}
