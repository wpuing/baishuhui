package com.baishuhui.domain.user.entity;

import com.baishuhui.common.persistence.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商家表实体。
 *
 * @author wei yz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bsh_merchant")
public class MerchantEntity extends BaseEntity {

    private String phone;
    private String shopName;
    private String status;
}
