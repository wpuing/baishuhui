package com.baishuhui.application.service.auth;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.user.vo.admin.AdminUserBriefDTO;
import com.baishuhui.user.vo.auth.CaptchaDTO;
import com.baishuhui.user.vo.auth.ChangePasswordRequest;
import com.baishuhui.user.vo.auth.LoginRequest;
import com.baishuhui.user.vo.auth.LoginResultDTO;
import com.baishuhui.user.vo.auth.RegisterRequest;
import com.baishuhui.user.vo.auth.UpdateProfileRequest;
import com.baishuhui.user.vo.auth.UserViewDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用服务接口（原 IUserAsvc）。
 *
 * @author wei yz
 */
public interface IUserAsvc {
    CaptchaDTO createCaptcha();
    String register(RegisterRequest request);
    LoginResultDTO login(LoginRequest request, String clientIp);
    UserViewDTO currentUser();
    UserViewDTO updateProfile(UpdateProfileRequest request);
    void changePassword(ChangePasswordRequest request);
    List<AdminUserBriefDTO> listAdminUsers();
}
