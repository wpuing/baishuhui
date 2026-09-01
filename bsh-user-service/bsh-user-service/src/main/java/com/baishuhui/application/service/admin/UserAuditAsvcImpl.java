package com.baishuhui.application.service.admin;

import com.baishuhui.application.service.wallet.IWalletAsvc;
import com.baishuhui.user.vo.wallet.PaymentResultDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.persistence.OperatorContext;
import com.baishuhui.common.util.IdUtil;
import com.baishuhui.domain.area.entity.AreaEntity;
import com.baishuhui.domain.area.repositories.IAreaRepository;
import com.baishuhui.domain.support.PageData;
import com.baishuhui.user.constant.UserStatusConstants;
import com.baishuhui.domain.user.entity.UserEntity;
import com.baishuhui.domain.user.repositories.IUserRepository;
import com.baishuhui.infrastructure.security.OperatorAuthSupport;
import com.baishuhui.infrastructure.security.TokenCutoffService;
import com.baishuhui.user.vo.admin.AuditRejectRequest;
import com.baishuhui.user.vo.admin.AuditUpdateRequest;
import com.baishuhui.user.vo.admin.AuditUserDTO;
import com.baishuhui.user.vo.admin.PageResultDTO;
import com.baishuhui.user.vo.admin.WalletAdjustRequest;
import com.baishuhui.user.vo.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 业务用户注册与管理端审核编排。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuditAsvcImpl implements IUserAuditAsvc {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAX_PAGE_SIZE = 50;

    private static final Set<String> REGISTER_ROLES = Set.of(
            UserStatusConstants.ROLE_FARMER,
            UserStatusConstants.ROLE_CONSUMER,
            UserStatusConstants.ROLE_MERCHANT);

    private final IUserRepository userRepository;

    private final IAreaRepository areaRepository;

    private final PasswordEncoder passwordEncoder;

    private final IWalletAsvc walletAsvc;
    private final TokenCutoffService tokenCutoffService;

    /**
     * 业务用户注册：默认待审核。
     *
     * @param request 注册入参
     * @return 新用户 id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String register(RegisterRequest request) {
        String role = request.getRole().trim().toUpperCase(Locale.ROOT);
        // 条件不满足时走异常或跳过
        if (!REGISTER_ROLES.contains(role)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "角色仅支持 FARMER/CONSUMER/MERCHANT");
        }
        AreaEntity area = areaRepository.getById(request.getAreaId().trim());
        // 空值分支判断
        if (area == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "地区不存在，请先在系统维护地区");
        }
        // 空值分支判断
        if (userRepository.getByUsername(request.getUsername().trim()) != null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "用户名已存在");
        }
        // 空值分支判断
        if (userRepository.getByPhone(request.getPhone().trim()) != null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "手机号已注册");
        }
        String roleId = userRepository.getRoleIdByCode(role);
        // 字符串非空才继续处理
        if (!StringUtils.hasText(roleId)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "角色未配置");
        }
        UserEntity entity = new UserEntity();
        entity.setId(IdUtil.nextId());
        entity.setUsername(request.getUsername().trim());
        entity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        entity.setPhone(request.getPhone().trim());
        entity.setRole(role);
        entity.setStatus(UserStatusConstants.PENDING);
        entity.setNickname(StringUtils.hasText(request.getNickname())
                ? request.getNickname().trim()
                : request.getUsername().trim());
        entity.setArea(area.getId());
        userRepository.insert(entity);
        userRepository.insertUserRole(entity.getId(), roleId);
        // 注册成功后初始化钱包并按配置注入测试资金（同一用户幂等）
        walletAsvc.grantTestFunds(entity.getId());
        log.info("user registered id={} role={} area={}", entity.getId(), role, area.getId());
        return entity.getId();
    }

    /**
     * 审核列表分页。普通管理员仅业务角色；超管可含普通管理员。
     */
    @Override
    public PageResultDTO<AuditUserDTO> pageAudits(String status, String role, int pageNum, int pageSize) {
        int num = Math.max(pageNum, 1);
        int size = pageSize <= 0 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        Set<String> visibleRoles = visibleListRoles();
        PageData<UserEntity> mpPage = userRepository.pageAudits(status, role, visibleRoles, num, size);
        // 空值分支判断
        if (mpPage.records() == null || mpPage.records().isEmpty()) {
            return PageResultDTO.of(List.of(), 0, num, size);
        }
        List<String> ids = mpPage.records().stream().map(UserEntity::getId).toList();
        Map<String, List<String>> roleMap = loadRoles(ids);
        List<AuditUserDTO> list = new ArrayList<>(mpPage.records().size());
        // 遍历集合逐项处理
        for (UserEntity u : mpPage.records()) {
            list.add(toAuditDto(u, roleMap.getOrDefault(u.getId(), Collections.emptyList())));
        }
        return PageResultDTO.of(list, mpPage.total(), num, size);
    }

    /**
     * 开始审核：待审核 → 审核中。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuditUserDTO start(String userId) {
        UserEntity user = requireBusinessUser(userId);
        // 字段相等性校验
        if (!UserStatusConstants.PENDING.equalsIgnoreCase(user.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "仅待审核账号可开始审核");
        }
        user.setStatus(UserStatusConstants.REVIEWING);
        fillAuditMeta(user, null);
        userRepository.updateById(user);
        log.info("audit start userId={}", userId);
        return toAuditDto(user, userRepository.listRoleCodesByUserId(userId));
    }

    /**
     * 审核通过。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuditUserDTO approve(String userId) {
        UserEntity user = requireBusinessUser(userId);
        // 字段相等性校验
        if (!UserStatusConstants.PENDING.equalsIgnoreCase(user.getStatus())
                && !UserStatusConstants.REVIEWING.equalsIgnoreCase(user.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "当前状态不可通过");
        }
        user.setStatus(UserStatusConstants.APPROVED);
        fillAuditMeta(user, "通过");
        userRepository.updateById(user);
        log.info("audit approve userId={}", userId);
        return toAuditDto(user, userRepository.listRoleCodesByUserId(userId));
    }

    /**
     * 审核驳回。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuditUserDTO reject(String userId, AuditRejectRequest request) {
        UserEntity user = requireBusinessUser(userId);
        // 字段相等性校验
        if (!UserStatusConstants.PENDING.equalsIgnoreCase(user.getStatus())
                && !UserStatusConstants.REVIEWING.equalsIgnoreCase(user.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "当前状态不可驳回");
        }
        String remark = request == null ? null : request.getRemark();
        user.setStatus(UserStatusConstants.REJECTED);
        fillAuditMeta(user, StringUtils.hasText(remark) ? remark.trim() : "不通过");
        userRepository.updateById(user);
        log.info("audit reject userId={}", userId);
        return toAuditDto(user, userRepository.listRoleCodesByUserId(userId));
    }

    /**
     * 用户详情：业务用户；超管还可查看普通管理员。
     */
    @Override
    public AuditUserDTO detail(String userId) {
        UserEntity user = requireVisibleUser(userId);
        return toAuditDto(user, userRepository.listRoleCodesByUserId(userId));
    }

    /**
     * 测试期向指定用户渠道入账，结单尾款不足时可由管理员调账。
     *
     * @param userId  用户 id
     * @param request 金额、渠道、备注
     * @return 入账结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PaymentResultDTO adjustWallet(String userId, WalletAdjustRequest request) {
        requireVisibleUser(userId);
        PaymentResultDTO result = walletAsvc.adjust(
                userId, request.getAmount(), request.getChannel(), request.getRemark());
        log.info("admin wallet adjust userId={} amount={} channel={}",
                userId, request.getAmount(), request.getChannel());
        return result;
    }

    /**
     * 修改用户资料（昵称 / 手机 / 地区）。仅超管；不可改超管或自己。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuditUserDTO update(String userId, AuditUpdateRequest request) {
        UserEntity user = requireMutableUser(userId);
        String phone = request.getPhone().trim();
        UserEntity other = userRepository.getByPhone(phone);
        // 空值分支判断
        if (other != null && !other.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "手机号已被占用");
        }
        // 字符串非空才继续处理
        if (StringUtils.hasText(request.getAreaId())) {
            AreaEntity area = areaRepository.getById(request.getAreaId().trim());
            // 空值分支判断
            if (area == null) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "地区不存在");
            }
            user.setArea(area.getId());
        }
        user.setNickname(request.getNickname().trim());
        user.setPhone(phone);
        userRepository.updateById(user);
        log.info("audit user updated id={} operator={}", userId, OperatorContext.get().userId());
        return toAuditDto(user, userRepository.listRoleCodesByUserId(userId));
    }

    /**
     * 逻辑删除用户。仅超管；不可删超管或自己。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String userId) {
        UserEntity user = requireMutableUser(userId);
        userRepository.deleteUserRoles(userId);
        userRepository.deleteById(user.getId());
        tokenCutoffService.revoke(user.getId());
        log.info("audit user deleted id={} operator={}", userId, OperatorContext.get().userId());
    }

    /**
     * 待审数量（含审核中）。
     */
    @Override
    public long countPendingAudit() {
        return userRepository.countPendingAudit();
    }

    private UserEntity requireBusinessUser(String userId) {
        UserEntity user = requireExistingUser(userId);
        // 条件不满足时走异常或跳过
        if (!UserStatusConstants.businessRole(user.getRole())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "系统人员无需审核");
        }
        return user;
    }

    private UserEntity requireVisibleUser(String userId) {
        UserEntity user = requireExistingUser(userId);
        // 业务条件分支
        if (isProtectedSuperAdmin(user)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "超级管理员账号不可查看");
        }
        // 业务条件分支
        if (UserStatusConstants.businessRole(user.getRole())) {
            return user;
        }
        // 业务条件分支
        if (UserStatusConstants.adminRole(user.getRole()) && operatorIsSuperAdmin()) {
            return user;
        }
        throw new BusinessException(ErrorCode.BUSINESS_ERROR, "系统人员无需审核");
    }

    private UserEntity requireMutableUser(String userId) {
        // 条件不满足时走异常或跳过
        if (!operatorIsSuperAdmin()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅超级管理员可修改或删除用户");
        }
        UserEntity user = requireVisibleUser(userId);
        String operatorId = OperatorContext.get().userId();
        // 字符串非空才继续处理
        if (StringUtils.hasText(operatorId) && operatorId.equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "不能修改或删除当前登录账号");
        }
        // 业务条件分支
        if (isProtectedSuperAdmin(user)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "超级管理员账号不可修改或删除");
        }
        return user;
    }

    private UserEntity requireExistingUser(String userId) {
        UserEntity user = userRepository.getById(userId);
        // 空值分支判断
        if (user == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "用户不存在");
        }
        return user;
    }

    private Set<String> visibleListRoles() {
        Set<String> roles = new HashSet<>(REGISTER_ROLES);
        // 业务条件分支
        if (operatorIsSuperAdmin()) {
            roles.add(UserStatusConstants.ROLE_ADMIN);
        }
        return roles;
    }

    private boolean operatorIsSuperAdmin() {
        return OperatorAuthSupport.operatorIsSuperAdmin(
                SecurityContextHolder.getContext().getAuthentication());
    }

    private boolean isProtectedSuperAdmin(UserEntity user) {
        // 业务条件分支
        if (UserStatusConstants.superAdminRole(user.getRole())) {
            return true;
        }
        List<String> codes = userRepository.listRoleCodesByUserId(user.getId());
        return codes != null && codes.stream().anyMatch(UserStatusConstants::superAdminRole);
    }

    private void fillAuditMeta(UserEntity user, String remark) {
        user.setAuditTime(LocalDateTime.now());
        String op = OperatorContext.get().userId();
        user.setAuditUser(StringUtils.hasText(op) ? op : "SYSTEM");
        // 空值分支判断
        if (remark != null) {
            user.setAuditRemark(remark);
        }
    }

    private Map<String, List<String>> loadRoles(List<String> userIds) {
        List<IUserRepository.UserRoleCode> rows = userRepository.listRoleCodesByUserIds(userIds);
        Map<String, List<String>> map = new HashMap<>();
        // 空值分支判断
        if (rows == null) {
            return map;
        }
        // 遍历集合逐项处理
        for (IUserRepository.UserRoleCode row : rows) {
            // 空值分支判断
            if (row.userId() == null || row.code() == null) {
                continue;
            }
            map.computeIfAbsent(row.userId(), k -> new ArrayList<>()).add(row.code());
        }
        return map;
    }

    private AuditUserDTO toAuditDto(UserEntity u, List<String> roles) {
        AuditUserDTO dto = new AuditUserDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setPhone(u.getPhone());
        dto.setRole(u.getRole());
        dto.setStatus(u.getStatus());
        dto.setNickname(u.getNickname());
        dto.setArea(u.getArea());
        dto.setAuditRemark(u.getAuditRemark());
        dto.setAuditTime(u.getAuditTime());
        dto.setAuditUser(u.getAuditUser());
        dto.setCreateTime(u.getCreateTime());
        dto.setRoles(roles == null ? Collections.emptyList() : roles);
        // 字符串非空才继续处理
        if (StringUtils.hasText(u.getArea())) {
            AreaEntity area = areaRepository.getById(u.getArea());
            // 空值分支判断
            if (area != null) {
                dto.setAreaName(area.getName());
            }
        }
        return dto;
    }
}
