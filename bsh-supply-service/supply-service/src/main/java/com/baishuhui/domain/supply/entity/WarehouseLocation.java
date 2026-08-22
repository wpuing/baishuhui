package com.baishuhui.domain.supply.entity;

import com.baishuhui.supply.constant.WarehouseStatusConstants;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.ddd.AggregateRoot;
import com.baishuhui.common.exception.BusinessException;
import lombok.Getter;

/**
 * 商家仓位。
 *
 * @author wei yz
 */
@Getter
public class WarehouseLocation extends AggregateRoot<String> {

    public static final String ENABLED = WarehouseStatusConstants.ENABLED;
    public static final String DISABLED = WarehouseStatusConstants.DISABLED;

    private String merchantId;
    private String name;
    private String remark;
    private String status;

    protected WarehouseLocation() {
    }

    /**
     * 新建仓位。
     */
    public static WarehouseLocation create(String id, String merchantId, String name, String remark) {
        if (merchantId == null || merchantId.isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "商家不能为空");
        }
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_LOCATION, "仓位名称不能为空");
        }
        WarehouseLocation loc = new WarehouseLocation();
        loc.setId(id);
        loc.merchantId = merchantId;
        loc.name = name.trim();
        loc.remark = remark == null ? "" : remark.trim();
        loc.status = ENABLED;
        return loc;
    }

    /**
     * 仓储回放。
     */
    public static WarehouseLocation restore(String id, String merchantId, String name, String remark, String status) {
        WarehouseLocation loc = new WarehouseLocation();
        loc.setId(id);
        loc.merchantId = merchantId;
        loc.name = name;
        loc.remark = remark;
        loc.status = status == null ? ENABLED : status;
        return loc;
    }

    /**
     * 改名。
     */
    public void rename(String name, String remark) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_LOCATION, "仓位名称不能为空");
        }
        this.name = name.trim();
        this.remark = remark == null ? "" : remark.trim();
    }

    /**
     * 停用仓位。
     */
    public void disable() {
        this.status = DISABLED;
    }

    /**
     * 校验归属。
     */
    public void assertOwnedBy(String merchantId) {
        if (merchantId == null || !merchantId.equals(this.merchantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能操作自己的仓位");
        }
    }
}
