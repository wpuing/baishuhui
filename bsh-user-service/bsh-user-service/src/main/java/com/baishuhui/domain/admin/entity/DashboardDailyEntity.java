package com.baishuhui.domain.admin.entity;

import com.baishuhui.common.persistence.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 运营总览按日快照。
 *
 * @author wei yz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bsh_dashboard_daily")
public class DashboardDailyEntity extends BaseEntity {

    @TableField("stat_date")
    private LocalDate statDate;

    @TableField("login_count")
    private Long loginCount;

    @TableField("pending_audit")
    private Long pendingAudit;

    @TableField("published_supply")
    private Long publishedSupply;

    @TableField("banner_count")
    private Long bannerCount;

    @TableField("audit_pie_json")
    private String auditPieJson;

    @TableField("category_rank_json")
    private String categoryRankJson;
}
