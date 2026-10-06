package com.baishuhui.application.service.banner;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.home.vo.UpsertBannerRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 应用服务接口（原 IBannerAdminAsvc）。
 *
 * @author wei yz
 */
public interface IBannerAdminAsvc {
    List<Map<String, Object>> listAll();
    Map<String, Object> create(UpsertBannerRequest request);
    Map<String, Object> update(String id, UpsertBannerRequest request);
    void delete(String id);
}
