package com.baishuhui.infrastructure.db.mapper.admin;

import com.baishuhui.domain.admin.entity.IpRuleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * IP 访问规则 Mapper。
 *
 * @author wei yz
 */
@Mapper
public interface IpRuleMapper extends BaseMapper<IpRuleEntity> {
}
