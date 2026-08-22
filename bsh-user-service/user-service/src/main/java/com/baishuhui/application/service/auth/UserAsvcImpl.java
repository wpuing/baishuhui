package com.baishuhui.application.service.auth;

import com.baishuhui.application.service.admin.IUserAuditAsvc;
import com.baishuhui.application.service.admin.IVisitStatsAsvc;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.user.constant.UserStatusConstants;
import com.baishuhui.domain.user.entity.UserEntity;
import com.baishuhui.domain.user.repositories.IUserRepository;
import com.baishuhui.domain.user.service.IAuthDsvc;
import com.baishuhui.infrastructure.security.AuthUserPrincipal;
import com.baishuhui.infrastructure.security.CaptchaService;
import com.baishuhui.infrastructure.security.JwtService;
import com.baishuhui.infrastructure.security.OperatorAuthSupport;
import com.baishuhui.infrastructure.security.TokenCutoffService;
import com.baishuhui.user.vo.admin.AdminUserBriefDTO;
import com.baishuhui.user.vo.auth.CaptchaDTO;
import com.baishuhui.user.vo.auth.ChangePasswordRequest;
import com.baishuhui.user.vo.auth.LoginRequest;
import com.baishuhui.user.vo.auth.LoginResultDTO;
import com.baishuhui.user.vo.auth.RegisterRequest;
import com.baishuhui.user.vo.auth.UpdateProfileRequest;
import com.baishuhui.user.vo.auth.UserViewDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户应用服务：验证码、登录、当前用户、管理端列表编排。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAsvcImpl implements IUserAsvc {

    private static final String TOKEN_TYPE_BEARER = "Bearer";

    private final CaptchaService captchaService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IUserRepository userRepository;
    private final IAuthDsvc authDsvc;
    private final IUserAuditAsvc userAuditAsvc;
    private final IVisitStatsAsvc visitStatsAsvc;
    private final PasswordEncoder passwordEncoder;
    private final TokenCutoffService tokenCutoffService;

    /**
     * 生成一次性图形验证码。
     *
     * @return 验证码 key 与图片
     */
    @Override
    public CaptchaDTO createCaptcha() {
        CaptchaService.CaptchaPayload payload = captchaService.create();
        return new CaptchaDTO(payload.captchaKey(), payload.imageBase64());
    }

    /**
     * 业务注册：校验验证码后落库待审。
     *
     * @param request 注册入参
     * @return 用户 id
     */
    @Override
    public String register(RegisterRequest request) {
        if (!captchaService.verifyAndConsume(request.getCaptchaKey(), request.getCaptchaCode())) {
            throw new BusinessException(ErrorCode.CAPTCHA_INVALID, "验证码错误或已过期");
        }
        return userAuditAsvc.register(request);
    }

    /**
     * 校验验证码并签发 JWT；成功后记入访问流水（每次登录 +1），并记录登录 IP。
     *
     * @param request  登录入参
     * @param clientIp 客户端 IP
     * @return 令牌与用户视图
     */
    @Override
    public LoginResultDTO login(LoginRequest request, String clientIp) {
        if (!captchaService.verifyAndConsume(request.getCaptchaKey(), request.getCaptchaCode())) {
            throw new BusinessException(ErrorCode.CAPTCHA_INVALID, "验证码错误或已过期");
        }
        // 领域校验审核态，避免仅 DisabledException
        UserEntity dbUser = userRepository.getByUsername(request.getUsername().trim());
        authDsvc.assertLoginAllowed(dbUser);
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            AuthUserPrincipal principal = (AuthUserPrincipal) auth.getPrincipal();
            String token = jwtService.issue(principal);
            visitStatsAsvc.recordLogin(principal.getId(), principal.getUsername(), clientIp);
            log.info("user login ok id={} ip={}", principal.getId(), clientIp);
            LoginResultDTO result = new LoginResultDTO();
            result.setAccessToken(token);
            result.setTokenType(TOKEN_TYPE_BEARER);
            result.setExpiresIn(jwtService.getExpireSeconds());
            result.setUser(toUserView(principal));
            return result;
        } catch (BadCredentialsException ex) {
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS, "用户名或密码错误");
        }
    }

    /**
     * 当前登录用户；未认证抛 UNAUTHORIZED。
     *
     * @return 用户视图
     */
    @Override
    public UserViewDTO currentUser() {
        return toUserView(requirePrincipal());
    }

    /**
     * 更新昵称 / 手机号。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserViewDTO updateProfile(UpdateProfileRequest request) {
        AuthUserPrincipal principal = requirePrincipal();
        UserEntity entity = userRepository.getById(principal.getId());
        if (entity == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "用户不存在");
        }
        if (StringUtils.hasText(request.getNickname())) {
            entity.setNickname(request.getNickname().trim());
        }
        if (StringUtils.hasText(request.getPhone())) {
            String phone = request.getPhone().trim();
            UserEntity other = userRepository.getByPhone(phone);
            if (other != null && !other.getId().equals(entity.getId())) {
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "手机号已被占用");
            }
            entity.setPhone(phone);
        }
        userRepository.updateById(entity);
        log.info("profile updated userId={}", entity.getId());
        return toUserView(principal);
    }

    /**
     * 修改密码。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changePassword(ChangePasswordRequest request) {
        AuthUserPrincipal principal = requirePrincipal();
        UserEntity entity = userRepository.getById(principal.getId());
        if (entity == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "用户不存在");
        }
        if (!authDsvc.matchesPassword(request.getOldPassword(), entity.getPasswordHash())) {
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS, "原密码不正确");
        }
        userRepository.updatePassword(entity.getId(), passwordEncoder.encode(request.getNewPassword()));
        tokenCutoffService.revoke(entity.getId());
        log.info("password changed userId={}", entity.getId());
    }

    /**
     * 管理端用户简要列表（批量查角色，避免 N+1）。
     *
     * @return 用户简要信息
     */
    @Override
    public List<AdminUserBriefDTO> listAdminUsers() {
        List<UserEntity> users = userRepository.listBriefUsers();
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> ids = users.stream().map(UserEntity::getId).toList();
        Map<String, List<String>> roleMap = loadRoles(ids);
        boolean superAdmin = OperatorAuthSupport.operatorIsSuperAdmin(
                SecurityContextHolder.getContext().getAuthentication());
        List<AdminUserBriefDTO> list = new ArrayList<>(users.size());
        for (UserEntity u : users) {
            List<String> roles = roleMap.getOrDefault(u.getId(), Collections.emptyList());
            if (!superAdmin && isSuperAdminAccount(u, roles)) {
                continue;
            }
            AdminUserBriefDTO dto = new AdminUserBriefDTO();
            dto.setId(u.getId());
            dto.setUsername(u.getUsername());
            dto.setPhone(u.getPhone());
            dto.setRole(u.getRole());
            dto.setStatus(u.getStatus());
            dto.setNickname(u.getNickname());
            dto.setCreateTime(u.getCreateTime());
            dto.setCreateUser(u.getCreateUser());
            dto.setDataYear(u.getDataYear());
            dto.setRoles(roles);
            list.add(dto);
        }
        return list;
    }

    private boolean isSuperAdminAccount(UserEntity user, List<String> roles) {
        if (UserStatusConstants.superAdminRole(user.getRole())) {
            return true;
        }
        return roles != null && roles.stream().anyMatch(UserStatusConstants::superAdminRole);
    }

    private Map<String, List<String>> loadRoles(List<String> userIds) {
        List<IUserRepository.UserRoleCode> rows = userRepository.listRoleCodesByUserIds(userIds);
        Map<String, List<String>> map = new HashMap<>();
        for (IUserRepository.UserRoleCode row : rows) {
            if (row.userId() == null || row.code() == null) {
                continue;
            }
            map.computeIfAbsent(row.userId(), k -> new ArrayList<>()).add(row.code());
        }
        return map;
    }

    private AuthUserPrincipal requirePrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthUserPrincipal principal)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        return principal;
    }

    private UserViewDTO toUserView(AuthUserPrincipal principal) {
        UserViewDTO dto = new UserViewDTO();
        dto.setId(principal.getId());
        dto.setUsername(principal.getUsername());
        dto.setNickname(principal.getNickname());
        dto.setRoles(principal.getRoles());
        dto.setPermissions(principal.getPermissions());
        UserEntity entity = userRepository.getById(principal.getId());
        if (entity != null) {
            dto.setNickname(entity.getNickname());
            dto.setPhone(entity.getPhone());
            dto.setRole(entity.getRole());
            dto.setStatus(entity.getStatus());
            dto.setArea(entity.getArea());
        }
        return dto;
    }
}
