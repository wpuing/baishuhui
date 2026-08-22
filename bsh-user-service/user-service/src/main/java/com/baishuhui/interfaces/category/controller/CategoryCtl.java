package com.baishuhui.interfaces.category.controller;

import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.category.CategoryDTO;
import com.baishuhui.application.service.category.ICategoryAsvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公开品类接口，供农户/商家发布与筛选下拉。
 *
 * @author wei yz
 */
@Tag(name = "品类")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryCtl {

    private final ICategoryAsvc categoryAsvc;

    /**
     * 启用中的品类列表。
     *
     * @return 品类
     */
    @Operation(summary = "启用中的品类")
    @GetMapping
    public Result<List<CategoryDTO>> list() {
        return Result.success(categoryAsvc.listEnabled());
    }
}
