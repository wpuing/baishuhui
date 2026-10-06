package com.baishuhui.order.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 交易五态流转节点（时间轴展示）。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeNodeDTO {
    /** 状态码 */
    private String status;
    /** 中文标题 */
    private String label;
    /** 节点说明 */
    private String detail;
    /** 发生时间；历史单中间态可能为空 */
    private LocalDateTime occurredAt;
    /** 地点（线上 / 产地） */
    private String location;
    /** 是否已到达（含跳过） */
    private boolean reached;
    /** 是否当前所在节点 */
    private boolean current;
    /** 是否跳过（如已拍下直接确认，未经过待确认） */
    private boolean skipped;
}
