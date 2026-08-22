package com.baishuhui.supply.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仓位。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseLocationDTO {
    private String id;
    private String merchantId;
    private String name;
    private String remark;
    private String status;
}
