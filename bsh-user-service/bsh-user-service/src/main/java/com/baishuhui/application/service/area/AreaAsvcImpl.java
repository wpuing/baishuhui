package com.baishuhui.application.service.area;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.user.vo.admin.AreaDTO;
import com.baishuhui.user.vo.admin.IpAreaHintDTO;
import com.baishuhui.user.vo.admin.UpsertAreaRequest;
import com.baishuhui.domain.area.entity.AreaEntity;
import com.baishuhui.domain.area.repositories.IAreaRepository;
import com.baishuhui.infrastructure.remote.BaiduIpLocationClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 地区维护与 IP 默认提示。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AreaAsvcImpl implements IAreaAsvc {

    private final IAreaRepository areaRepository;

    private final BaiduIpLocationClient baiduIpLocationClient;

    /**
     * 地区树（公开注册 / 管理共用）。
     */
    @Override
    public List<AreaDTO> tree() {
        List<AreaEntity> all = areaRepository.listAllOrdered();
        // 空值分支判断
        if (all == null || all.isEmpty()) {
            return List.of();
        }
        Map<String, String> parentOf = new HashMap<>(all.size() * 2);
        Map<String, AreaDTO> map = new HashMap<>(all.size() * 2);
        List<AreaDTO> roots = new ArrayList<>();
        // 遍历集合逐项处理
        for (AreaEntity e : all) {
            map.put(e.getId(), toDto(e));
            parentOf.put(e.getId(), e.getParentId());
        }
        // 遍历集合逐项处理
        for (AreaEntity e : all) {
            AreaDTO node = map.get(e.getId());
            String parentId = e.getParentId();
            // 字符串非空才继续处理
            if (!StringUtils.hasText(parentId) || !map.containsKey(parentId) || wouldCycle(parentOf, e.getId(), parentId)) {
                // 字符串非空才继续处理
                if (StringUtils.hasText(parentId) && map.containsKey(parentId)) {
                    log.warn("area parent cycle detected id={} parentId={}", e.getId(), parentId);
                }
                roots.add(node);
            } else {
                map.get(parentId).getChildren().add(node);
            }
        }
        sortTree(roots, new HashSet<>());
        return roots;
    }

    /**
     * 新增地区。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AreaDTO create(UpsertAreaRequest request) {
        validate(request);
        // 业务条件分支
        if (areaRepository.existsCode(request.getCode().trim(), null)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "地区编码已存在");
        }
        AreaEntity entity = new AreaEntity();
        apply(entity, request);
        areaRepository.insert(entity);
        log.info("area created id={} code={}", entity.getId(), entity.getCode());
        return toDto(entity);
    }

    /**
     * 更新地区。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AreaDTO update(String id, UpsertAreaRequest request) {
        validate(request);
        AreaEntity entity = areaRepository.getById(id);
        // 空值分支判断
        if (entity == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "地区不存在");
        }
        // 业务条件分支
        if (areaRepository.existsCode(request.getCode().trim(), id)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "地区编码已存在");
        }
        apply(entity, request);
        areaRepository.updateById(entity);
        log.info("area updated id={}", id);
        return toDto(entity);
    }

    /**
     * 逻辑删除地区。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        AreaEntity entity = areaRepository.getById(id);
        // 空值分支判断
        if (entity == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "地区不存在");
        }
        // 业务条件分支
        if (areaRepository.countByParentId(id) > 0) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "请先删除下级地区");
        }
        areaRepository.deleteById(id);
        log.info("area deleted id={}", id);
    }

    /**
     * 按客户端 IP 查询百度归属地并匹配系统地区（失败可空）。
     */
    @Override
    public IpAreaHintDTO ipHint(String clientIp) {
        IpAreaHintDTO dto = new IpAreaHintDTO();
        dto.setClientIp(clientIp);
        BaiduIpLocationClient.Location loc = baiduIpLocationClient.resolve(clientIp);
        // 空值分支判断
        if (loc == null) {
            dto.setMessage("未获取到 IP 归属地，请手动选择地区");
            return dto;
        }
        dto.setProvince(loc.province());
        dto.setCity(loc.city());
        AreaEntity matched = matchArea(loc.city(), loc.province());
        // 空值分支判断
        if (matched != null) {
            dto.setAreaId(matched.getId());
            dto.setAreaName(matched.getName());
            dto.setMessage("已根据 IP 预填地区，可修改");
        } else {
            dto.setMessage("归属地未在系统维护，请手动选择或联系管理员添加");
        }
        return dto;
    }

    private AreaEntity matchArea(String city, String province) {
        // 字符串非空才继续处理
        if (StringUtils.hasText(city)) {
            AreaEntity byCity = areaRepository.findFirstByNameLike(trimRegion(city));
            // 空值分支判断
            if (byCity != null) {
                return byCity;
            }
        }
        // 字符串非空才继续处理
        if (StringUtils.hasText(province)) {
            return areaRepository.findFirstProvinceByNameLike(trimRegion(province));
        }
        return null;
    }

    private static String trimRegion(String name) {
        String n = name.trim();
        // 遍历集合逐项处理
        for (String suffix : List.of("省", "市", "自治区", "壮族自治区", "回族自治区", "维吾尔自治区", "特别行政区")) {
            // 业务条件分支
            if (n.endsWith(suffix)) {
                return n.substring(0, n.length() - suffix.length());
            }
        }
        return n;
    }

    private void validate(UpsertAreaRequest request) {
        // 空值分支判断
        if (request.getLevel() == null || request.getLevel() < 1 || request.getLevel() > 3) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "level 须为 1~3");
        }
        // 字符串非空才继续处理
        if (StringUtils.hasText(request.getParentId())) {
            AreaEntity parent = areaRepository.getById(request.getParentId().trim());
            // 空值分支判断
            if (parent == null) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "父级地区不存在");
            }
        }
    }

    private static void apply(AreaEntity entity, UpsertAreaRequest request) {
        entity.setParentId(StringUtils.hasText(request.getParentId()) ? request.getParentId().trim() : null);
        entity.setCode(request.getCode().trim());
        entity.setName(request.getName().trim());
        entity.setLevel(request.getLevel());
        entity.setSortNo(request.getSortNo() == null ? 0 : request.getSortNo());
    }

    private static boolean wouldCycle(Map<String, String> parentOf, String nodeId, String parentId) {
        String cur = parentId;
        Set<String> seen = new HashSet<>();
        // 循环处理
        while (StringUtils.hasText(cur)) {
            // 字段相等性校验
            if (nodeId.equals(cur)) {
                return true;
            }
            // 条件不满足时走异常或跳过
            if (!seen.add(cur)) {
                return true;
            }
            cur = parentOf.get(cur);
        }
        return false;
    }

    private static AreaDTO toDto(AreaEntity e) {
        AreaDTO dto = new AreaDTO();
        dto.setId(e.getId());
        dto.setParentId(e.getParentId());
        dto.setCode(e.getCode());
        dto.setName(e.getName());
        dto.setLevel(e.getLevel());
        dto.setSortNo(e.getSortNo());
        return dto;
    }

    private static void sortTree(List<AreaDTO> nodes, Set<String> visiting) {
        // 空值分支判断
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        nodes.sort(Comparator
                .comparing((AreaDTO a) -> a.getSortNo() == null ? 0 : a.getSortNo())
                .thenComparing(a -> a.getCode() == null ? "" : a.getCode()));
        // 遍历集合逐项处理
        for (AreaDTO n : nodes) {
            // 空值分支判断
            if (n.getId() != null && !visiting.add(n.getId())) {
                log.warn("area sort cycle at id={}", n.getId());
                n.setChildren(new ArrayList<>());
                continue;
            }
            sortTree(n.getChildren(), visiting);
            // 空值分支判断
            if (n.getId() != null) {
                visiting.remove(n.getId());
            }
        }
    }
}
