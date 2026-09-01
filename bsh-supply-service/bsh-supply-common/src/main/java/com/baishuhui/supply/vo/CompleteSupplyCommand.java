package com.baishuhui.supply.vo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 完成供应命令。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteSupplyCommand {
    @NotEmpty
    private List<String> completionImageUrls;
    /** 是否售罄；默认未售罄 */
    @Builder.Default
    private boolean soldOut = false;
    @Size(max = 32)
    private String orderId;
}
