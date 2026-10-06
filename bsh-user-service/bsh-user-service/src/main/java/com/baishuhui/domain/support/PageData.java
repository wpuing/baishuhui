package com.baishuhui.domain.support;

import java.util.List;

/**
 * 领域分页结果（不依赖 MyBatis Page）。
 *
 * @param records 当前页
 * @param total   总数
 * @param <T>     实体类型
 * @author wei yz
 */
public record PageData<T>(List<T> records, long total) {
}
