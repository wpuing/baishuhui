package com.baishuhui.supply.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 供应信息传输对象。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplyInfoDTO {
    private String id;
    private String merchantId;
    private String title;
    private String description;
    private String contactPhone;
    private String location;
    private String category;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal depositAmount;
    private String status;
    private LocalDateTime publishTime;
    private String lockedByBuyerId;
    private String lockOrderId;
    private List<String> fieldImages;
    private List<String> completionImages;
}
