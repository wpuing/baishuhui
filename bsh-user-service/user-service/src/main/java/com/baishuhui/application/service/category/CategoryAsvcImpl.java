package com.baishuhui.application.service.category;

import com.baishuhui.user.vo.category.CategoryDTO;
import com.baishuhui.domain.category.entity.CategoryEntity;
import com.baishuhui.domain.category.repositories.ICategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 农产品品类查询。
 *
 * @author wei yz
 */
@Service
@RequiredArgsConstructor
public class CategoryAsvcImpl implements ICategoryAsvc {

    private final ICategoryRepository categoryRepository;

    /**
     * 启用中的品类，按 sort_no、名称排序。
     *
     * @return 品类列表，不会返回 null
     */
    @Override
    public List<CategoryDTO> listEnabled() {
        return categoryRepository.listEnabled().stream().map(this::toDto).collect(Collectors.toList());
    }

    private CategoryDTO toDto(CategoryEntity entity) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSortNo(entity.getSortNo());
        return dto;
    }
}
