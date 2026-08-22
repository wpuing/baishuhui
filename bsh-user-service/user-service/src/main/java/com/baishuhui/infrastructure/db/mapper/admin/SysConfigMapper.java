package com.baishuhui.infrastructure.db.mapper.admin;

import com.baishuhui.domain.admin.entity.SysConfigEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统参数 Mapper。
 *
 * @author wei yz
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfigEntity> {
}
