package com.baishuhui.application.service.area;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.user.vo.admin.AreaDTO;
import com.baishuhui.user.vo.admin.IpAreaHintDTO;
import com.baishuhui.user.vo.admin.UpsertAreaRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用服务接口（原 IAreaAsvc）。
 *
 * @author wei yz
 */
public interface IAreaAsvc {
    List<AreaDTO> tree();
    AreaDTO create(UpsertAreaRequest request);
    AreaDTO update(String id, UpsertAreaRequest request);
    void delete(String id);
    IpAreaHintDTO ipHint(String clientIp);
}
