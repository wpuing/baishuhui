package com.baishuhui.user.vo.admin;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 审核驳回备注。
 *
 * @author wei yz
 */
@Data
public class AuditRejectRequest {

    @Size(max = 256)
    private String remark;
}
