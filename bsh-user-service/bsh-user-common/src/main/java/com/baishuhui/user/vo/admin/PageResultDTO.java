package com.baishuhui.user.vo.admin;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 管理端通用分页结果。
 *
 * @author wei yz
 */
@Data
public class PageResultDTO<T> {

    private List<T> records = Collections.emptyList();

    private long total;

    private long pageNum;

    private long pageSize;

    private long pages;

    /**
     * 组装分页结果。
     */
    public static <T> PageResultDTO<T> of(List<T> records, long total, long pageNum, long pageSize) {
        PageResultDTO<T> dto = new PageResultDTO<>();
        dto.setRecords(records == null ? Collections.emptyList() : records);
        dto.setTotal(total);
        dto.setPageNum(pageNum);
        dto.setPageSize(pageSize);
        long size = pageSize <= 0 ? 1 : pageSize;
        dto.setPages(total == 0 ? 0 : (total + size - 1) / size);
        return dto;
    }
}
