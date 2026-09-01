package com.baishuhui.common.persistence;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * MySQL 表映射实体公共基础字段。所有业务表实体须继承本类。
 *
 * @author wei yz
 */
@Data
public abstract class BaseEntity implements Serializable {

    /** 主键：32 位无横线 UUID */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 创建时间 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 创建用户 id */
    @TableField(value = "create_user", fill = FieldFill.INSERT)
    private String createUser;

    /** 创建用户名称 */
    @TableField(value = "create_user_name", fill = FieldFill.INSERT)
    private String createUserName;

    /** 更新时间 */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 更新用户 id */
    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    private String updateUser;

    /** 是否删除：0 未删除，1 已删除 */
    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

    /** 删除时间（逻辑删除时写入） */
    @TableField(value = "delete_time", fill = FieldFill.UPDATE)
    private LocalDateTime deleteTime;

    /** 地区 id，关联 bsh_area.id */
    @TableField("area")
    private String area;

    /** 数据年度，如 2026 */
    @TableField(value = "data_year", fill = FieldFill.INSERT)
    private Integer dataYear;
}
