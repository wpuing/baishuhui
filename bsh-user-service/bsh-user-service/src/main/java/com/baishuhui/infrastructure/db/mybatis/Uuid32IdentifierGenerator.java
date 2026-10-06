package com.baishuhui.infrastructure.db.mybatis;

import com.baishuhui.common.util.IdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.stereotype.Component;

/**
 * 生成 32 位无横线 UUID 主键。
 *
 * @author wei yz
 */
@Component
public class Uuid32IdentifierGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        // 本系统主键为字符串，不走数字序列
        return null;
    }

    @Override
    public String nextUUID(Object entity) {
        return IdUtil.nextId();
    }
}
