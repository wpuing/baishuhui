package com.baishuhui.application.service.admin;

import com.baishuhui.user.vo.wallet.PaymentResultDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.persistence.OperatorContext;
import com.baishuhui.common.util.IdUtil;
import com.baishuhui.user.vo.admin.AuditRejectRequest;
import com.baishuhui.user.vo.admin.AuditUpdateRequest;
import com.baishuhui.user.vo.admin.AuditUserDTO;
import com.baishuhui.user.vo.admin.AdminResetPasswordRequest;
import com.baishuhui.user.vo.admin.PageResultDTO;
import com.baishuhui.user.vo.admin.WalletAdjustRequest;
import com.baishuhui.user.vo.auth.RegisterRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
 * 应用服务接口（原 IUserAuditAsvc）。
 *
 * @author wei yz
 */
public interface IUserAuditAsvc {
    String register(RegisterRequest request);
    PageResultDTO<AuditUserDTO> pageAudits(String status, String role, int pageNum, int pageSize);
    AuditUserDTO start(String userId);
    AuditUserDTO approve(String userId);
    AuditUserDTO reject(String userId, AuditRejectRequest request);
    AuditUserDTO detail(String userId);
    PaymentResultDTO adjustWallet(String userId, WalletAdjustRequest request);
    AuditUserDTO update(String userId, AuditUpdateRequest request);
    void delete(String userId);

    /** 锁定账号并作废 Token。 */
    AuditUserDTO lock(String userId);

    /** 解锁：业务用户 APPROVED，管理员 ACTIVE。 */
    AuditUserDTO unlock(String userId);

    /** 管理员重置密码并作废 Token。 */
    AuditUserDTO resetPassword(String userId, AdminResetPasswordRequest request);

    long countPendingAudit();
}
