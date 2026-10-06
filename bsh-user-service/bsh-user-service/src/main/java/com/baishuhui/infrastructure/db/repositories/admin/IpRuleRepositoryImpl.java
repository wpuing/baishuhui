package com.baishuhui.infrastructure.db.repositories.admin;

import com.baishuhui.domain.admin.entity.IpRuleEntity;
import com.baishuhui.domain.admin.repositories.IIpRuleRepository;
import com.baishuhui.domain.support.PageData;
import com.baishuhui.infrastructure.db.mapper.admin.IpRuleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * IP 规则仓储实现。
 *
 * @author wei yz
 */
@Repository
@RequiredArgsConstructor
public class IpRuleRepositoryImpl implements IIpRuleRepository {

    private final IpRuleMapper ipRuleMapper;

    @Override
    public List<IpRuleEntity> listActive() {
        List<IpRuleEntity> rows = ipRuleMapper.selectList(baseSelect());
        return rows == null ? Collections.emptyList() : rows;
    }

    @Override
    public PageData<IpRuleEntity> page(String ruleType, int pageNum, int pageSize) {
        LambdaQueryWrapper<IpRuleEntity> wrapper = baseSelect();
        // 字符串非空才继续处理
        if (StringUtils.hasText(ruleType)) {
            wrapper.eq(IpRuleEntity::getRuleType, ruleType.trim().toUpperCase(Locale.ROOT));
        }
        wrapper.orderByDesc(IpRuleEntity::getCreateTime);
        Page<IpRuleEntity> mpPage = ipRuleMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        // 空值分支判断
        if (mpPage == null || mpPage.getRecords() == null) {
            return new PageData<>(Collections.emptyList(), 0L);
        }
        return new PageData<>(mpPage.getRecords(), mpPage.getTotal());
    }

    @Override
    public IpRuleEntity getById(String id) {
        return ipRuleMapper.selectById(id);
    }

    @Override
    public IpRuleEntity findExact(String ip, String ruleType) {
        return ipRuleMapper.selectOne(baseSelect()
                .eq(IpRuleEntity::getIp, ip)
                .eq(IpRuleEntity::getRuleType, ruleType)
                .last("LIMIT 1"));
    }

    @Override
    public void insert(IpRuleEntity entity) {
        ipRuleMapper.insert(entity);
    }

    @Override
    public int updateById(IpRuleEntity entity) {
        return ipRuleMapper.updateById(entity);
    }

    @Override
    public int deleteById(String id) {
        return ipRuleMapper.deleteById(id);
    }

    private static LambdaQueryWrapper<IpRuleEntity> baseSelect() {
        return new LambdaQueryWrapper<IpRuleEntity>()
                .select(IpRuleEntity::getId, IpRuleEntity::getIp, IpRuleEntity::getRuleType,
                        IpRuleEntity::getSource, IpRuleEntity::getReason, IpRuleEntity::getExpireTime,
                        IpRuleEntity::getHitCount, IpRuleEntity::getCreateTime, IpRuleEntity::getCreateUserName);
    }
}
