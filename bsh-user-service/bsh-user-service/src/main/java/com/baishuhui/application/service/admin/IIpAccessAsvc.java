package com.baishuhui.application.service.admin;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.user.vo.admin.IpRuleDTO;
import com.baishuhui.user.vo.admin.PageResultDTO;
import com.baishuhui.user.vo.admin.UpsertIpRuleRequest;
import com.baishuhui.user.vo.internal.AutoBanIpRequest;
import com.baishuhui.user.vo.internal.IpRuleSnapshotDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 应用服务接口（原 IIpAccessAsvc）。
 *
 * @author wei yz
 */
public interface IIpAccessAsvc {
    void warmupRedis();
    PageResultDTO<IpRuleDTO> page(String ruleType, int pageNum, int pageSize);
    List<IpRuleDTO> list(String ruleType);
    IpRuleDTO addWhitelist(UpsertIpRuleRequest request);
    IpRuleDTO addBlacklist(UpsertIpRuleRequest request);
    void remove(String id);
    void autoBan(AutoBanIpRequest request);
    IpRuleSnapshotDTO snapshot();
}
