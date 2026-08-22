package com.baishuhui.application.service.admin;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.domain.admin.entity.IpRuleEntity;
import com.baishuhui.domain.admin.repositories.IIpRuleRepository;
import com.baishuhui.domain.support.PageData;
import com.baishuhui.infrastructure.security.IpAccessConstants;
import com.baishuhui.infrastructure.security.IpAccessRedisSupport;
import com.baishuhui.user.vo.admin.IpRuleDTO;
import com.baishuhui.user.vo.admin.PageResultDTO;
import com.baishuhui.user.vo.admin.UpsertIpRuleRequest;
import com.baishuhui.user.vo.internal.AutoBanIpRequest;
import com.baishuhui.user.vo.internal.IpRuleSnapshotDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * IP 白名单 / 黑名单编排：管理端 CRUD、网关快照、自动拉黑落库。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IpAccessAsvcImpl implements IIpAccessAsvc {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAX_PAGE_SIZE = 50;

    private static final Pattern IPV4 = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.){3}(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(/(3[0-2]|[12]?\\d))?$");

    private static final Pattern IPV6 = Pattern.compile("^[0-9a-fA-F:]{2,64}$");

    private final IIpRuleRepository ipRuleRepository;

    private final IpAccessRedisSupport ipAccessRedisSupport;

    /**
     * 启动时把有效规则刷进 Redis。
     */
    @PostConstruct
    @Override
    public void warmupRedis() {
        ipAccessRedisSupport.reload(ipRuleRepository.listActive());
    }

    /**
     * 管理端分页列表。
     *
     * @param ruleType 可选 WHITELIST / BLACKLIST
     * @param pageNum  页码，从 1 起
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @Override
    public PageResultDTO<IpRuleDTO> page(String ruleType, int pageNum, int pageSize) {
        int num = Math.max(pageNum, 1);
        int size = pageSize <= 0 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        PageData<IpRuleEntity> mpPage = ipRuleRepository.page(ruleType, num, size);
        if (mpPage.records() == null || mpPage.records().isEmpty()) {
            return PageResultDTO.of(List.of(), 0, num, size);
        }
        List<IpRuleDTO> list = new ArrayList<>(mpPage.records().size());
        for (IpRuleEntity row : mpPage.records()) {
            list.add(toDto(row));
        }
        return PageResultDTO.of(list, mpPage.total(), num, size);
    }

    /**
     * 兼容旧调用：不分页时最多返回 500 条。
     *
     * @param ruleType 可选类型
     * @return 规则列表
     */
    @Override
    public List<IpRuleDTO> list(String ruleType) {
        PageResultDTO<IpRuleDTO> page = page(ruleType, 1, 500);
        return page.getRecords();
    }

    /**
     * 手工加入白名单；若同 IP 在黑名单则先移除。
     *
     * @param request 入参
     * @return 新规则
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public IpRuleDTO addWhitelist(UpsertIpRuleRequest request) {
        String ip = normalizeAndValidate(request.getIp());
        removeByIpAndType(ip, IpAccessConstants.TYPE_BLACKLIST);
        IpRuleEntity exist = ipRuleRepository.findExact(ip, IpAccessConstants.TYPE_WHITELIST);
        if (exist != null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "该 IP 已在白名单");
        }
        IpRuleEntity entity = newEntity(ip, IpAccessConstants.TYPE_WHITELIST, IpAccessConstants.SOURCE_MANUAL,
                request.getReason(), null, 0);
        ipRuleRepository.insert(entity);
        log.info("ip whitelist added ip={}", ip);
        ipAccessRedisSupport.reload(ipRuleRepository.listActive());
        return toDto(entity);
    }

    /**
     * 手工加入黑名单。白名单中的 IP 不可直接拉黑。
     *
     * @param request 入参
     * @return 新规则
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public IpRuleDTO addBlacklist(UpsertIpRuleRequest request) {
        String ip = normalizeAndValidate(request.getIp());
        if (ipRuleRepository.findExact(ip, IpAccessConstants.TYPE_WHITELIST) != null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "该 IP 已在白名单，请先移除白名单");
        }
        IpRuleEntity exist = ipRuleRepository.findExact(ip, IpAccessConstants.TYPE_BLACKLIST);
        LocalDateTime expire = toExpire(request.getExpireMinutes());
        if (exist != null) {
            exist.setReason(trimReason(request.getReason()));
            exist.setExpireTime(expire);
            exist.setSource(IpAccessConstants.SOURCE_MANUAL);
            ipRuleRepository.updateById(exist);
            ipAccessRedisSupport.reload(ipRuleRepository.listActive());
            return toDto(exist);
        }
        IpRuleEntity entity = newEntity(ip, IpAccessConstants.TYPE_BLACKLIST, IpAccessConstants.SOURCE_MANUAL,
                request.getReason(), expire, 0);
        ipRuleRepository.insert(entity);
        log.info("ip blacklist added ip={}", ip);
        ipAccessRedisSupport.reload(ipRuleRepository.listActive());
        return toDto(entity);
    }

    /**
     * 逻辑删除规则。
     *
     * @param id 规则 id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "规则 id 不能为空");
        }
        IpRuleEntity exist = ipRuleRepository.getById(id.trim());
        if (exist == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "规则不存在");
        }
        ipRuleRepository.deleteById(id.trim());
        log.info("ip rule removed id={} ip={} type={}", id, exist.getIp(), exist.getRuleType());
        ipAccessRedisSupport.reload(ipRuleRepository.listActive());
    }

    /**
     * 网关自动拉黑：白名单 IP 忽略；已有黑名单则延长过期。
     *
     * @param request 网关上报
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void autoBan(AutoBanIpRequest request) {
        String ip = normalizeAndValidate(request.getIp());
        if (ipRuleRepository.findExact(ip, IpAccessConstants.TYPE_WHITELIST) != null) {
            log.info("auto-ban skipped whitelist ip={}", ip);
            return;
        }
        int hits = request.getHitCount() == null ? 0 : request.getHitCount();
        LocalDateTime expire = toExpire(request.getExpireMinutes() == null ? 24 * 60 : request.getExpireMinutes());
        IpRuleEntity exist = ipRuleRepository.findExact(ip, IpAccessConstants.TYPE_BLACKLIST);
        if (exist != null) {
            exist.setHitCount(Math.max(hits, exist.getHitCount() == null ? 0 : exist.getHitCount()));
            exist.setExpireTime(expire);
            exist.setReason(trimReason(request.getReason()));
            if (!IpAccessConstants.SOURCE_MANUAL.equals(exist.getSource())) {
                exist.setSource(IpAccessConstants.SOURCE_AUTO);
            }
            ipRuleRepository.updateById(exist);
            ipAccessRedisSupport.reload(ipRuleRepository.listActive());
            return;
        }
        IpRuleEntity entity = newEntity(ip, IpAccessConstants.TYPE_BLACKLIST, IpAccessConstants.SOURCE_AUTO,
                request.getReason(), expire, hits);
        ipRuleRepository.insert(entity);
        log.warn("ip auto-banned ip={} hits={}", ip, hits);
        ipAccessRedisSupport.reload(ipRuleRepository.listActive());
    }

    /**
     * 网关定时拉取的有效规则快照。
     *
     * @return 白名单 IP 与未过期黑名单
     */
    @Override
    public IpRuleSnapshotDTO snapshot() {
        List<IpRuleEntity> rules = ipRuleRepository.listActive();
        IpRuleSnapshotDTO dto = new IpRuleSnapshotDTO();
        LocalDateTime now = LocalDateTime.now();
        for (IpRuleEntity rule : rules) {
            if (!StringUtils.hasText(rule.getIp())) {
                continue;
            }
            String ip = rule.getIp().trim();
            if (IpAccessConstants.TYPE_WHITELIST.equals(rule.getRuleType())) {
                dto.getWhitelist().add(ip);
                continue;
            }
            if (!IpAccessConstants.TYPE_BLACKLIST.equals(rule.getRuleType())) {
                continue;
            }
            if (rule.getExpireTime() != null && !rule.getExpireTime().isAfter(now)) {
                continue;
            }
            IpRuleSnapshotDTO.BlacklistEntry entry = new IpRuleSnapshotDTO.BlacklistEntry();
            entry.setIp(ip);
            if (rule.getExpireTime() != null) {
                entry.setExpireEpochMilli(rule.getExpireTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
            dto.getBlacklist().add(entry);
        }
        return dto;
    }

    private void removeByIpAndType(String ip, String type) {
        IpRuleEntity exist = ipRuleRepository.findExact(ip, type);
        if (exist != null && exist.getId() != null) {
            ipRuleRepository.deleteById(exist.getId());
        }
    }

    private static IpRuleEntity newEntity(String ip, String type, String source, String reason,
            LocalDateTime expire, int hitCount) {
        IpRuleEntity entity = new IpRuleEntity();
        entity.setIp(ip);
        entity.setRuleType(type);
        entity.setSource(source);
        entity.setReason(trimReason(reason));
        entity.setExpireTime(expire);
        entity.setHitCount(hitCount);
        return entity;
    }

    private static IpRuleDTO toDto(IpRuleEntity entity) {
        IpRuleDTO dto = new IpRuleDTO();
        dto.setId(entity.getId());
        dto.setIp(entity.getIp());
        dto.setRuleType(entity.getRuleType());
        dto.setSource(entity.getSource());
        dto.setReason(entity.getReason());
        dto.setExpireTime(entity.getExpireTime());
        dto.setHitCount(entity.getHitCount());
        dto.setCreateTime(entity.getCreateTime());
        dto.setCreateUserName(entity.getCreateUserName());
        return dto;
    }

    private static String normalizeAndValidate(String raw) {
        if (!StringUtils.hasText(raw)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "IP 不能为空");
        }
        String ip = raw.trim().toLowerCase(Locale.ROOT);
        if (ip.startsWith("::ffff:")) {
            ip = ip.substring("::ffff:".length());
        }
        if (!IPV4.matcher(ip).matches() && !IPV6.matcher(ip).matches()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "IP 格式不正确，支持 IPv4 / IPv6 / IPv4 CIDR");
        }
        return ip;
    }

    private static String trimReason(String reason) {
        if (!StringUtils.hasText(reason)) {
            return null;
        }
        String t = reason.trim();
        return t.length() > 256 ? t.substring(0, 256) : t;
    }

    private static LocalDateTime toExpire(Integer expireMinutes) {
        if (expireMinutes == null || expireMinutes <= 0) {
            return null;
        }
        return LocalDateTime.now().plusMinutes(expireMinutes);
    }
}
